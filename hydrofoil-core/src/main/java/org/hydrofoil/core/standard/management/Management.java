package org.hydrofoil.core.standard.management;

import org.hydrofoil.common.configuration.HydrofoilConfiguration;

import java.io.Closeable;
import java.io.IOException;

/**
 * Management
 * <p>
 * package org.hydrofoil.core.management
 *
 * @author xie_yh
 * @date 2018/7/10 13:12
 */
public final class Management implements Closeable {

    /**
     * data source manager
     */
    private DataSourceManager dataSourceManager;

    /**
     * schema manager
     */
    private SchemaManager schemaManager;

    public Management(){
        this.dataSourceManager = new DataSourceManager(this);
        this.schemaManager = new SchemaManager();
    }

    public void load(HydrofoilConfiguration configuration) throws Exception{
        schemaManager.load(configuration);
    }

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

    @Override
    public void close() throws IOException {
        dataSourceManager.close();
    }
}
