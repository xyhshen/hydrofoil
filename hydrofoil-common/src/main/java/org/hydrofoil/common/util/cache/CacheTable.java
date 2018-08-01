package org.hydrofoil.common.util.cache;

import java.io.Closeable;
import java.io.IOException;
import java.util.Collection;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;

/**
 * CacheTable
 * <p>
 * package org.hydrofoil.common.util.cache
 *
 * @author xie_yh
 * @date 2018/7/31 14:47
 */
public final class CacheTable implements Closeable {

    private final String cacheName;

    private final long expireTime;

    private final int maxSize;

    private final ConcurrentMap<Object,CacheNode> cacheNodeMap;

    CacheTable(final String cacheName,final long expireTime,final int maxSize){
        this.cacheName = cacheName;
        this.expireTime = expireTime;
        this.maxSize = maxSize;
        this.cacheNodeMap = new ConcurrentHashMap<>(maxSize / 2);
    }

    public Object put(final Object key,Object value){
        if(value == null){
            return null;
        }
        if(cacheNodeMap.size() >= maxSize){
            return value;
        }
        CacheNode cacheNode = new CacheNode(key,value);
        return cacheNodeMap.replace(key,cacheNode).getValue();
    }

    private Object defaultWrite(final Object key, final Function<Object, Object> mappingFunction){
        if(mappingFunction == null){
            return null;
        }
        return put(key,mappingFunction.apply(key));
    }

    public Object get(final Object key){
        return get(key);
    }

    public Object get(final Object key, final Function<Object, Object> mappingFunction){
        CacheNode cacheNode = cacheNodeMap.get(key);
        if(cacheNode == null){
            return defaultWrite(key,mappingFunction);
        }
        if(cacheNode.isExpired(expireTime)){
            cacheNodeMap.remove(key);
            return defaultWrite(key,mappingFunction);
        }
        return cacheNode.getValue();
    }

    public void refresh(){
        Collection<CacheNode> values = cacheNodeMap.values();
        values.stream().filter(p->p.isExpired(expireTime)).
                forEach(c->cacheNodeMap.remove(c.getId()));
    }

    public void clear(){
        cacheNodeMap.clear();
    }

    @Override
    public void close() throws IOException {
        GlobalCacheManager.getInstance().closeCache(cacheName);
    }
}
