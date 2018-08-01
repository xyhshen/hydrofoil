package org.hydrofoil.common.util.cache;

import java.util.HashMap;
import java.util.Map;

/**
 * GlobalCacheManager
 * <p>
 * package org.hydrofoil.common.util.cache
 *
 * @author xie_yh
 * @date 2018/7/31 14:44
 */
public final class GlobalCacheManager  {

    private final static long REFRESH_INTERVAL = (2 * 60 * 1000);

    private final Map<String,CacheTable> cacheTableMap;

    private static GlobalCacheManager instance = null;

    private GlobalCacheManager(){
        cacheTableMap = new HashMap<>();
    }

    private void init(){
        Thread refreshThread = new Thread(() -> {
            long lastRefresh = System.currentTimeMillis();
            while (true) {
                if ((System.currentTimeMillis() - lastRefresh) >= REFRESH_INTERVAL) {
                    try {
                        cacheTableMap.values().forEach(CacheTable::refresh);
                    } catch (Throwable t) {
                    }
                    lastRefresh = System.currentTimeMillis();
                }
            }
        });
        refreshThread.setDaemon(true);
        refreshThread.start();
    }

    public static synchronized GlobalCacheManager getInstance(){
        if(instance == null){
            instance = new GlobalCacheManager();
            instance.init();
        }
        return instance;
    }

    public CacheTable registerCache(String name,
                                    long expireTime,
                                    int maxSize){
        synchronized (cacheTableMap){
            CacheTable cacheTable = new CacheTable(
                    name,
                    expireTime,
                    maxSize
            );
            cacheTableMap.put(name,cacheTable);
            return cacheTable;
        }
    }

    public void closeCache(String name){
        synchronized (cacheTableMap){
            CacheTable cacheTable = cacheTableMap.get(name);
            if(cacheTable != null){
                cacheTable.clear();
                cacheTableMap.remove(name);
            }
        }
    }

}
