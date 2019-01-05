package org.hydrofoil.provider.jdbc.internal;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.MultiMapUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang3.mutable.MutableInt;
import org.hydrofoil.common.provider.IDataConnectContext;
import org.hydrofoil.common.provider.datasource.*;
import org.hydrofoil.common.schema.ColumnSchema;
import org.hydrofoil.common.schema.TableSchema;
import org.hydrofoil.common.util.LogUtils;
import org.hydrofoil.common.util.ArgumentUtils;
import org.hydrofoil.common.util.bean.KeyValueEntity;
import org.hydrofoil.provider.jdbc.IJdbcService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.*;

/**
 * DbQueryService
 * <p>
 * package org.hydrofoil.provider.jdbc.internal
 *
 * @author xie_yh
 * @date 2018/7/5 11:46
 */
public abstract class AbstractJdbcService implements IJdbcService {

    private static Logger logger = LoggerFactory.getLogger(AbstractJdbcService.class);

    private IDataConnectContext dataSourceContext;

    private DataSource dataSource;

    protected SqlFeature sqlFeature;

    protected static final String KEY_NAME = "keys";

    protected AbstractJdbcService(
            DataSource dataSource,
            IDataConnectContext dataSourceContext){
        this.dataSource = dataSource;
        this.dataSourceContext = dataSourceContext;
        this.sqlFeature = new SqlFeature();
    }

    protected String toRealName(final String tableName){
        final Map<String, TableSchema> tableSchema = dataSourceContext.getTableSchema();
        return tableSchema.get(tableName).getRealName();
    }

    /**
     * generate alias of table and column
     */
    private QueryContext createContext(BaseRowQuery rowQuery){
        QueryContext context = new QueryContext();
        //field to column
        final RowColumnInformation columnInformation = rowQuery.getColumnInformation();
        context.getColumns().putAll(toRealName(rowQuery.getName()),columnInformation.columns(toRealName(rowQuery.getName())));
        rowQuery.getAssociateQuery().forEach((v)->{
            context.getColumns().putAll(toRealName(v.getName()),columnInformation.columns(toRealName(v.getName())));
        });
        //generate main table name
        String mainName = toRealName(rowQuery.getName());
        context.getTableAlias().put(mainName,"mtb");
        //generate associate table name
        int i = 0;
        for(RowQueryScan.AssociateRowQuery query:rowQuery.getAssociateQuery()){
            context.getTableAlias().put(toRealName(query.getName()),"stb" + i++);
        }
        //generate column name
        i = 0;
        MutableInt columnIdCnt = new MutableInt(1);
        for(String tableName:context.getColumns().keySet()){
            Map<String,String> columnMap = context.getColumnAlias().computeIfAbsent(tableName,(k->new TreeMap<>()));
            final int i1 = i;
            MultiMapUtils.getCollection(context.getColumns(),tableName).forEach((columnName)->{
                columnMap.put(columnName, "cl_" + Integer.toHexString(columnIdCnt.toInteger()));
                columnIdCnt.add(1);
            });
            i++;
        }

        return context;
    }

    /**
     * create sql
     * @param rowQuery
     * @param context
     * @return
     */
    protected abstract SqlStatement createBasicGetSqlTemplate(RowQueryGet rowQuery, QueryContext context);

    /**
     * make get sql
     * @param tableName
     * @param basicSql
     * @param context
     * @param keyValueEntities
     */
    protected abstract void makeGetKeySql(final String tableName,SqlStatement basicSql,final QueryContext context,final Collection<KeyValueEntity> keyValueEntities);

    /**
     * create count sql
     * @param params params list
     * @return sql
     */
    protected abstract String crateCountSql(List<String> params);

    @Override
    public RowQueryResponse getRows(RowQueryGet rowQueryGet) {
        List<RowStore> rowStores = new ArrayList<>();
        try{
            final QueryContext context = createContext(rowQueryGet);
            ArgumentUtils.notEmpty(rowQueryGet.getRowKeys(),"row key");
            //create basic query
            final SqlStatement sqlStatement = createBasicGetSqlTemplate(rowQueryGet, context);
            final List<List<KeyValueEntity>> pages = ListUtils.partition(new ArrayList<>(rowQueryGet.getRowKeys()),
                    sqlFeature.getMaxPerPage());
            for(List<KeyValueEntity> page:pages){
                //make get key sql
                makeGetKeySql(toRealName(rowQueryGet.getName()),sqlStatement,context,page);
                //execute sql
                final List<Map<String, Object>> results = executeSqlStatement(sqlStatement);
                rowStores.addAll(processResult(rowQueryGet,context,results));
            }
        }catch (SQLException e){
            return RowQueryResponse.createFailedResponse(rowQueryGet.getId(),e);
        }
        return rowQueryGet.createResponse(rowStores);
    }

    protected abstract SqlStatement createBasicScanSql(final RowQueryScan rowQueryScan,final QueryContext context);

    protected abstract SqlStatement createQueryScanSql(final RowQueryScan rowQueryScan,final QueryContext context);

    protected abstract void makeScanKeySql(SqlStatement basicSql,final QueryContext context,final Collection<KeyValueEntity> keyValueEntities);

    @Override
    public RowQueryResponse scanRows(RowQueryScan rowQueryScan) {
        List<RowStore> rowStores = new ArrayList<>();
        try{
            final SqlStatement sqlStatement;
            final QueryContext context = createContext(rowQueryScan);
            if(rowQueryScan.isSimpleScan()){
                sqlStatement = createQueryScanSql(rowQueryScan,context);
            }else{
                //create basic query
                sqlStatement = createBasicScanSql(rowQueryScan, context);
            }

            final RowQueryScanKey scanKey = rowQueryScan.getScanKey();
            if(scanKey != null){
                final List<List<KeyValueEntity>> pages = ListUtils.partition(new ArrayList<>(scanKey.getKeyValueEntities()),
                        sqlFeature.getMaxPerPage());
                for(List<KeyValueEntity> page:pages){
                    //make get key sql
                    makeScanKeySql(sqlStatement,context,page);
                    //execute sql
                    final List<Map<String, Object>> results = executeSqlStatement(sqlStatement);
                    rowStores.addAll(processResult(rowQueryScan,context,results));
                }
            }else{
                final List<Map<String, Object>> results = executeSqlStatement(sqlStatement);
                rowStores.addAll(processResult(rowQueryScan,context,results));
            }
        }catch (SQLException e){
            return RowQueryResponse.createFailedResponse(rowQueryScan.getId(),e);
        }
        return rowQueryScan.createResponse(rowStores);
    }

    @Override
    public RowQueryResponse countRow(BaseRowQuery baseRowQuery, String groupField) {
        return null;
    }

    /**
     * execute sql query
     * @return result
     * @throws SQLException
     */
    private List<Map<String, Object>> executeSqlStatement(SqlStatement sqlStatement) throws SQLException {
        List<Object> params = sqlStatement.getParams();
        String sql = sqlStatement.toSql();

        //output current sql
        LogUtils.printDebug(logger,"current execute sql:" + sql);

        List<Map<String, Object>> results = new QueryRunner(dataSource).
                query(sql, new MapListHandler(), params.toArray());
        return results;
    }

    /**
     * execute count sql
     * @return total
     * @throws SQLException
     */
    public Long executeCount() throws SQLException{
        List<String> params = new ArrayList<>();
        String sql = crateCountSql(params);
        return new QueryRunner(dataSource).
                query(sql, new ScalarHandler<Long>(), params);
    }

    private static void putRow(
            final String name,
            final Map<String, String> columns,
            final Map<String,Object> values,
            final RowStore row){
        columns.forEach((column,alias)->{
            Object value = MapUtils.getObject(values,alias);
            row.put(name,column,value);
        });
    }

    /**
     * process result
     * @param results raw result
     * @return rowlist
     */
    private Collection<RowStore> processResult(BaseRowQuery rowQuery,QueryContext context,List<Map<String, Object>> results){
        List<RowStore> rowStores = new ArrayList<>(results.size());
        final RowColumnInformation columnInformation = rowQuery.getColumnInformation();
        results.forEach((m)->{
            final RowStore row = columnInformation.createRowStore();
            final Collection<BaseRowQuery.AssociateRowQuery> associateRowQueries =
                    rowQuery.getAssociateQuery();
            final Map<String, String> mainColumn = context.getColumnAlias().get(toRealName(rowQuery.getName()));
            putRow(rowQuery.getName(),mainColumn,m,row);
            for(BaseRowQuery.AssociateRowQuery associateRowQuery:associateRowQueries){
                final Map<String, String> allColumn = context.getColumnAlias().get(toRealName(associateRowQuery.getName()));
                putRow(associateRowQuery.getName(),allColumn,m,row);
            }
            rowStores.add(row);
        });
        return rowStores;
    }

    /**
     * transform result value to accpet type
     * @param tableName table name
     * @param columnName column name
     * @param value raw value
     * @return result
     */
    private Object transformValue(final String tableName,final String columnName,final Object value){
        try {
            return handleQueryResult(dataSourceContext.getColumnSchema(tableName, columnName),value);
        } catch (SQLException e) {
            LogUtils.callError(logger,"handleQueryResult",e);
        }
        return null;
    }

    /**
     * handle result value
     * @param columnSchema schema
     * @param value value
     * @return result,is accpet type
     */
    protected abstract Object handleQueryResult(final ColumnSchema columnSchema,final Object value) throws SQLException;


}
