package org.hydrofoil.provider.jdbc.internal;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.Map;

/**
 * QueryContext
 * <p>
 * package org.hydrofoil.provider.jdbc.internal
 *
 * @author xie_yh
 * @date 2018/12/31 13:49
 */
public final class QueryContext {

    private MultiValuedMap<String,String> columns;

    private Map<String,String> tableAlias;

    private Map<String,Map<String,String>> columnAlias;

    public QueryContext(){
        columnAlias = new HashMap<>();
        tableAlias = new HashMap<>();
        columns = new ArrayListValuedHashMap<>();
    }

    /**
     * @return String>
     * @see QueryContext#columns
     **/
    public MultiValuedMap<String, String> getColumns() {
        return columns;
    }

    /**
     * @param columns String>
     * @see QueryContext#columns
     **/
    public void setColumns(MultiValuedMap<String, String> columns) {
        this.columns = columns;
    }

    /**
     * @return String>
     * @see QueryContext#tableAlias
     **/
    public Map<String, String> getTableAlias() {
        return tableAlias;
    }

    /**
     * @param tableAlias String>
     * @see QueryContext#tableAlias
     **/
    public void setTableAlias(Map<String, String> tableAlias) {
        this.tableAlias = tableAlias;
    }

    /**
     * @return String>>
     * @see QueryContext#columnAlias
     **/
    public Map<String, Map<String, String>> getColumnAlias() {
        return columnAlias;
    }

    /**
     * @param columnAlias String>>
     * @see QueryContext#columnAlias
     **/
    public void setColumnAlias(Map<String, Map<String, String>> columnAlias) {
        this.columnAlias = columnAlias;
    }

    /**
     * get column alias
     * @param tableName table name
     * @param columnName column name
     * @return full name
     */
    public String getColumnAlias(String tableName,String columnName){
        if(StringUtils.isBlank(columnName)){
            return null;
        }
        return tableAlias.get(tableName) + "." + columnName;
    }
}
