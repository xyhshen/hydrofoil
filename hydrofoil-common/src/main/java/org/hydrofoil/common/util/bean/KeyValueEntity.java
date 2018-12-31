package org.hydrofoil.common.util.bean;

import org.apache.commons.collections4.CollectionUtils;
import org.hydrofoil.common.util.collect.FixedArrayMap;

import java.util.Collection;
import java.util.Map;

/**
 * RowKey
 * <p>
 * package org.hydrofoil.common.provider.datasource
 *
 * @author xie_yh
 * @date 2018/11/3 15:29
 */
public final class KeyValueEntity {

    private final Map<String,Object> keyValueMap;

    private final KeyValueEntityFactory factory;

    private KeyValueEntity(final KeyValueEntityFactory factory,final Map<String,Object> keyValueMap){
        this.factory = factory;
        this.keyValueMap = keyValueMap;
    }

    /**
     * @return KeyValueEntityFactory
     * @see KeyValueEntity#factory
     **/
    public KeyValueEntityFactory getFactory() {
        return factory;
    }

    public static class KeyValueEntityFactory{

        private final Map<String,Integer> keyMap;

        private KeyValueEntityFactory(final Collection<String> keys){
            keyMap = FixedArrayMap.keyMapOf(keys);
        }

        public KeyValueEntity create(){
            return new KeyValueEntity(this,new FixedArrayMap<>(keyMap));
        }

        public Collection<String> keys(){
            return keyMap.keySet();
        }
    }

    public static KeyValueEntityFactory createFactory(final Collection<String> keys){
        return new KeyValueEntityFactory(keys);
    }

    /**
     * @return String>
     * @see KeyValueEntity#KeyValueEntity
     **/
    public Map<String, Object> getKeyValueMap() {
        return keyValueMap;
    }

    /**
     * put a value
     * @param key key
     * @param value value
     * @return this
     */
    public KeyValueEntity put(final String key,final Object value){
        keyValueMap.put(key,value);
        return this;
    }

    @Override
    public boolean equals(Object o){
        if(!(o instanceof KeyValueEntity)){
            return false;
        }
        return CollectionUtils.isEqualCollection(((KeyValueEntity)o).keyValueMap.keySet(),keyValueMap.keySet())
                && CollectionUtils.isEqualCollection(((KeyValueEntity)o).keyValueMap.values(),keyValueMap.values());
    }

    @Override
    public int hashCode(){
        int hashCode = 1;
        for(Map.Entry<String,Object> entry:keyValueMap.entrySet()){
            hashCode = 31 * hashCode + entry.getKey().hashCode();
            hashCode = 31 * hashCode + (entry.getValue() == null ? 0 : entry.getValue().hashCode());
        }
        return hashCode;
    }
}
