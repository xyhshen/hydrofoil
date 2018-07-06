package org.hydrofoil.provider.mysql.internal;

import com.mysql.cj.api.result.Row;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.MultiMapUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;
import org.hydrofoil.common.provider.datasource.RowQueryRequest;
import org.hydrofoil.common.provider.datasource.RowStore;

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

    private BasicDataSource dataSource;

    protected RowQueryRequest request;

    protected MultiValuedMap<String,String> columns;

    protected Map<String,String> tableAlias;

    protected Map<String,Map<String,String>> columnAlias;

    AbstractDbQueryService(BasicDataSource dataSource,RowQueryRequest request){
        this.dataSource = dataSource;
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
        tableAlias.put(mainName,"main" + Integer.toHexString(RandomUtils.nextInt(1,100)).toLowerCase());
        //generate associate table name
        int i = 0;
        for(RowQueryRequest.AssociateRowQuery query:request.getAssociateQuery()){
            tableAlias.put(query.getName(),"secondary" + i++);
        }
        //generate column name
        i = 0;
        for(String tableName:columns.keySet()){
            Map<String,String> columnMap = columnAlias.computeIfAbsent(tableName,(k->new TreeMap<>()));
            final int i1 = i;
            MultiMapUtils.getCollection(columns,tableName).forEach((columnName)->{
                columnMap.put(columnName, "s" + i1 + RandomStringUtils.random(8,"abcdefghijklmnopqrst"));
            });
            i++;
        }
    }

    public void init(){
        initColumn();
    }

    /**
     * create sql
     * @param params params list
     * @return sql
     */
    protected abstract String crateSql(List<String> params);

    /**
     * execute sql query
     * @return result
     * @throws SQLException
     */
    public Collection<RowStore> executeQuery() throws SQLException {
        List<String> params = new ArrayList<>();
        String sql = crateSql(params);
        List<Map<String, Object>> results = new QueryRunner(dataSource).
                query(sql, new MapListHandler(), params);
        return processResult(results);
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


}
