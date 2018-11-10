package org.hydrofoil.core.engine.management;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.RandomUtils;
import org.hydrofoil.common.configuration.HydrofoilConfiguration;
import org.hydrofoil.common.configuration.HydrofoilConfigurationItem;
import org.hydrofoil.common.util.cache.CacheTable;
import org.hydrofoil.common.util.cache.GlobalCacheManager;

import java.io.Closeable;
import java.io.IOException;

/**
 * CacheManager
 * <p>
 * package org.hydrofoil.core.engine.management
 *
 * @author xie_yh
 * @date 2018/7/31 16:21
 */
public final class CacheManager implements Closeable {

    /**
     * vertex cache
     */
    private CacheTable vertexCache;

    /**
     * edge cache
     */
    private CacheTable edgeCache;

    CacheManager(){}

    void load(HydrofoilConfiguration configuration){
        int cacheMaxsize = configuration.getInt(HydrofoilConfigurationItem.QueryCacheMaxsize);
        long cacheTime = configuration.getLong(HydrofoilConfigurationItem.QueryCacheTime);
        vertexCache = GlobalCacheManager.getInstance().registerCache("vertex" + RandomUtils.nextInt(),cacheTime,
                cacheMaxsize);
        edgeCache = GlobalCacheManager.getInstance().registerCache("edge" + RandomUtils.nextInt(),cacheTime,
                cacheMaxsize);
    }

    public void putVertex(){

    }

    @Override
    public void close() throws IOException {
        IOUtils.closeQuietly(vertexCache);
        IOUtils.closeQuietly(edgeCache);
    }
}
