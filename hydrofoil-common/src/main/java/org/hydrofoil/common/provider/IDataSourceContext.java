package org.hydrofoil.common.provider;

import org.hydrofoil.common.schema.ColumnSchema;
import org.hydrofoil.common.schema.DataSourceSchema;

/**
 * IDataSourceContext
 * <p>
 * package org.hydrofoil.common.provider
 *
 * @author xie_yh
 * @date 2018/8/25 16:40
 */
public interface IDataSourceContext {

    /**
     * get table column schema
     * @param tableName table ' name
     * @param columnName column ' name
     * @return column schema
     */
    ColumnSchema getColumnSchema(final String tableName, final String columnName);

    /**
     * get current data source schema;
     * @return data source schema
     */
    DataSourceSchema getDatasourceSchema();

}
