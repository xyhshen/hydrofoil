package org.hydrofoil.common.util.collect;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.common.util.ParameterUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * FixedArrayMap
 * <p>
 * package org.hydrofoil.common.util.collect
 *
 * @author xie_yh
 * @date 2018/12/8 13:41
 */
public class FixedArrayMap<K,V> implements Map<K,V>, Cloneable, Serializable {

    private final Map<K,Integer> keyMap;

    private final FixedArrayMapValue[] values;

    class FixedArrayMapValue<V>{

        private V value;

        /**
         * @return V
         * @see FixedArrayMapValue#value
         **/
        V getValue() {
            return value;
        }

        V setValue(V value) {
            V old = this.value;
            this.value = value;
            return old;
        }

        @Override
        public String toString() {
            return Objects.toString(value);
        }
    }

    class FixedArrayMapEntry<K,V> implements Entry{

        private final K key;

        private final FixedArrayMapValue<V> mapValue;

        FixedArrayMapEntry(final K key,final FixedArrayMapValue<V> mapValue){
            this.key = key;
            this.mapValue = mapValue;
        }

        @Override
        public Object getKey() {
            return key;
        }

        @Override
        public Object getValue() {
            return mapValue.getValue();
        }

        @SuppressWarnings("unchecked")
        @Override
        public Object setValue(Object value) {
            return mapValue.setValue((V) value);
        }

        @Override
        public int hashCode() {
            return key.hashCode();
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object obj){
            if(!(obj instanceof FixedArrayMapEntry)){
                return false;
            }
            return Objects.equals(((FixedArrayMapEntry)obj).getKey(),getKey());
        }

        @Override
        public String toString() {
            return key + "=" + values;
        }
    }

    public FixedArrayMap(final Map<K,Integer> keyMap){
        ParameterUtils.mustTrue(MapUtils.isNotEmpty(keyMap));
        this.keyMap = keyMap;
        values = new FixedArrayMapValue[keyMap.size()];
    }

    public static <K> Map<K,Integer> keyMapOf(final Collection<K> keys){
        ParameterUtils.mustTrue(CollectionUtils.isNotEmpty(keys));
        final Map<K,Integer> keyMap = DataUtils.newHashMapWithExpectedSize(keys.size());
        for(K key:keys){
            Integer i = keyMap.size();
            keyMap.put(key,i);
        }
        return keyMap;
    }

    @Override
    public int size() {
        return (int) Stream.of(values).filter(Objects::nonNull).count();
    }

    @Override
    public boolean isEmpty() {
        return size() <= 0;
    }

    @Override
    public boolean containsKey(Object key) {
        return keyMap.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return Stream.of(values).filter(p-> Objects.equals(p,value)).count() > 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public V get(Object key) {
        Integer i = keyMap.get(key);
        if(i == null){
            return null;
        }
        if(values[i] == null){
            return null;
        }
        return (V) values[i].getValue();
    }

    @SuppressWarnings("unchecked")
    @Override
    public V put(K key, V value) {
        Integer i = keyMap.get(key);
        if(i == null){
            return null;
        }
        if(values[i] == null){
            values[i] = new FixedArrayMapValue();
        }
        return (V) values[i].setValue(value);
    }

    @SuppressWarnings("unchecked")
    @Override
    public V remove(Object key) {
        Integer i = keyMap.get(key);
        if(i == null){
            return null;
        }
        FixedArrayMapValue current = values[i];
        values[i] = null;
        return current != null?(V) current.getValue():null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        for(Entry<? extends K, ? extends V> entry:m.entrySet()){
            put(entry.getKey(),entry.getValue());
        }
    }

    @Override
    public void clear() {
        Arrays.fill(values,null);
    }

    @Override
    public Set<K> keySet() {
        return keyMap.keySet();
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<V> values() {
        return Stream.of(values).filter(Objects::nonNull).map(v->(V)v.getValue()).collect(Collectors.toList());
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<Entry<K, V>> entrySet() {
        final Set<Entry<K,V>> entries = DataUtils.newHashSetWithExpectedSize(keyMap.size());
        keyMap.forEach((key,i)->{
            FixedArrayMapValue value = values[i];
            if(value != null){
                entries.add(new FixedArrayMapEntry<>(key,value));
            }
        });
        return entries;
    }
}
