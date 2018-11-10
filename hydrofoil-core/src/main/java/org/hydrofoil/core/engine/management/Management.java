package org.hydrofoil.core.engine.management;

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
     * collect source manager
     */
    private DataSourceManager dataSourceManager;

    /**
     * schema manager
     */
    private SchemaManager schemaManager;

    /**
     * cache manager
     */
    private CacheManager cacheManager;

    private static final ThreadLocal<Management> THREAD_SHARE = new ThreadLocal<>();

    public Management(){
        this.dataSourceManager = new DataSourceManager(this);
        this.schemaManager = new SchemaManager();
        this.cacheManager = new CacheManager();
    }

    public void load(final HydrofoilConfiguration configuration) throws Exception{
        schemaManager.load(configuration);
        cacheManager.load(configuration);
    }

    /**
     * get collect source manger
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

    /**
     * @return CacheManager
     * @see Management#cacheManager
     **/
    public CacheManager getCacheManager() {
        return cacheManager;
    }

    @Override
    public void close() throws IOException {
        dataSourceManager.close();
        cacheManager.close();
    }

    /**
     * open management
     */
    public void openManagement(){
        THREAD_SHARE.set(this);
    }

    /**
     * get current management
     * @return Management
     */
    public static Management getManagement(){
        return THREAD_SHARE.get();
    }
}
