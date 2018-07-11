package org.hydrofoil.core.management;

/**
 * Management
 * <p>
 * package org.hydrofoil.core.management
 *
 * @author xie_yh
 * @date 2018/7/10 13:12
 */
public final class Management {

    /**
     * data source manager
     */
    private DataSourceManager dataSourceManager;

    /**
     * schema manager
     */
    private SchemaManager schemaManager;

    public Management(){}

    /**
     * get data source manger
     * @return DataSourceManager
     * @see Management#dataSourceManager
     **/
    public DataSourceManager getDataSourceManager() {
        return dataSourceManager;
    }

    /**
     * get schema manager
     * @return SchemaManager
     * @see Management#schemaManager
     **/
    public SchemaManager getSchemaManager() {
        return schemaManager;
    }
}
