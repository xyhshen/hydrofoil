package org.hydrofoil.common.util.cache;

/**
 * CacheNode
 * <p>
 * package org.hydrofoil.common.util.cache
 *
 * @author xie_yh
 * @date 2018/7/31 14:58
 */
public final class CacheNode {

    private final Object id;

    private volatile long lastAccess;

    private Object value;

    CacheNode(final Object id,Object value){
        this.id = id;
        lastAccess = System.currentTimeMillis();
        this.value = value;
    }

    boolean isExpired(Long expireTime){
        return (System.currentTimeMillis() - lastAccess) >= expireTime;
    }

    /**
     * @return Object
     * @see CacheNode#id
     **/
    Object getId() {
        return id;
    }

    /**
     * @return Object
     * @see CacheNode#value
     **/
    Object getValue() {
        return value;
    }
}
