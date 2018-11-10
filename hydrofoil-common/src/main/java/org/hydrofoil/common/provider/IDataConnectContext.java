package org.hydrofoil.common.provider;

import org.hydrofoil.common.schema.ColumnSchema;
import org.hydrofoil.common.schema.DataSourceSchema;
import org.hydrofoil.common.schema.NamespaceSchema;
import org.hydrofoil.common.schema.TableSchema;

import java.util.Map;

/**
 * IDataSourceContext
 * <p>
 * package org.hydrofoil.common.provider
 *
 * @author xie_yh
 * @date 2018/8/25 16:40
 */
public interface IDataConnectContext {

    /**
     * get table column schema
     * @param realname table real ' name
     * @param columnName column ' name
     * @return column schema
     */
    ColumnSchema getColumnSchema(final String realname, final String columnName);

    /**
     * get table schema by name's
     * @param tableNames name's
     * @return table schema'name and schema
     */
    Map<String,TableSchema> getTableSchema(final String ...tableNames);

    /**
     * get namespace by name's
     * @param namespaces name's
     * @return namespace schema'name and schema
     */
    Map<String,NamespaceSchema> getNamespaceSchema(final String ...namespaces);

    /**
     * get current data source schema;
     * @return data source schema
     */
    DataSourceSchema getDatasourceSchema();

}
