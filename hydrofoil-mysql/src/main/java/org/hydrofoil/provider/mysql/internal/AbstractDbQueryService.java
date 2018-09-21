package org.hydrofoil.provider.mysql.internal;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.MultiMapUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.lang3.mutable.MutableInt;
import org.hydrofoil.common.provider.IDataSourceContext;
import org.hydrofoil.common.provider.datasource.RowQueryRequest;
import org.hydrofoil.common.provider.datasource.RowStore;
import org.hydrofoil.common.schema.ColumnSchema;
import org.hydrofoil.common.util.LogUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.SQLException;
import java.util.*;

/**
 * DbQueryService
 * <p>
 * package org.hydrofoil.provider.mysql.internal
 *
 * @author xie_yh
 * @date 2018/7/5 11:46
 */
public abstract class AbstractDbQueryService {

    private static Logger logger = LoggerFactory.getLogger(AbstractDbQueryService.class);

    private BasicDataSource dataSource;

    protected RowQueryRequest request;

    protected MultiValuedMap<String,String> columns;

    protected Map<String,String> tableAlias;

    protected Map<String,Map<String,String>> columnAlias;

    private IDataSourceContext dataSourceContext;

    AbstractDbQueryService(
            BasicDataSource dataSource,
            IDataSourceContext dataSourceContext,
            RowQueryRequest request){
        this.dataSource = dataSource;
        this.dataSourceContext = dataSourceContext;
        this.request = request;
        columnAlias = new HashMap<>();
        tableAlias = new HashMap<>();
        columns = new ArrayListValuedHashMap<>();
    }

    /**
     * generate alias of table and column
     */
    private void initColumn(){
        //field to column
        columns.putAll(request.getName(),request.getFields());
        request.getAssociateQuery().forEach((v)->{
            columns.putAll(v.getName(),v.getFields());
        });
        //generate main table name
        String mainName = request.getName();
        tableAlias.put(mainName,"maintbl");
        //generate associate table name
        int i = 0;
        for(RowQueryRequest.AssociateRowQuery query:request.getAssociateQuery()){
            tableAlias.put(query.getName(),"sectbl" + i++);
        }
        //generate column name
        i = 0;
        MutableInt columnIdCnt = new MutableInt(1);
        for(String tableName:columns.keySet()){
            Map<String,String> columnMap = columnAlias.computeIfAbsent(tableName,(k->new TreeMap<>()));
            final int i1 = i;
            MultiMapUtils.getCollection(columns,tableName).forEach((columnName)->{
                columnMap.put(columnName, "ocol_" + Integer.toHexString(columnIdCnt.toInteger()));
                columnIdCnt.add(1);
            });
            i++;
        }
    }

    public void init(){
        initColumn();
    }

    /**
     * get column alias
     * @param tableName table name
     * @param columnName column name
     * @return full name
     */
    protected String getColumnAlias(String tableName,String columnName){
        return tableAlias.get(tableName) + "." + columnName;
    }

    /**
     * create sql
     * @param params params list
     * @return sql
     */
    protected abstract String crateQuerySql(List<String> params);

    /**
     * create count sql
     * @param params params list
     * @return sql
     */
    protected abstract String crateCountSql(List<String> params);

    /**
     * execute sql query
     * @return result
     * @throws SQLException
     */
    public Collection<RowStore> executeQuery() throws SQLException {
        List<String> params = new ArrayList<>();
        String sql = crateQuerySql(params);

        //output current sql
        LogUtils.printDebug(logger,"current execute sql:" + sql);

        List<Map<String, Object>> results = new QueryRunner(dataSource).
                query(sql, new MapListHandler(), params.toArray());
        return processResult(results);
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

    /**
     * process result
     * @param results raw result
     * @return rowlist
     */
    private Collection<RowStore> processResult(List<Map<String, Object>> results){
        List<RowStore> rowStores = new ArrayList<>(results.size());
        results.forEach((m)->{
            RowStore row = new RowStore();
            columnAlias.forEach((tableName,allColumn)->
                allColumn.forEach((column,alias)->{
                    Object value = MapUtils.getObject(m,alias);
                    row.put(tableName,column,value);
                })
            );
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
