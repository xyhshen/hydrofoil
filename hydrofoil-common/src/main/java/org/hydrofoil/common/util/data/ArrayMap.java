package org.hydrofoil.common.util.data;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.hydrofoil.common.util.ParameterUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * ArrayMap
 * <p>
 * package org.hydrofoil.common.util.data
 *
 * @author xie_yh
 * @date 2018/7/30 18:54
 */
public class ArrayMap<K,V> implements Map<K,V>, Cloneable, Serializable {

    private ArrayMapEntry<K,V>[] storager;

    private final class ArrayMapEntry<K,V> implements Entry<K,V>,Comparable<ArrayMapEntry>{

        private K key;

        private V value;

        private ArrayMapEntry(K key,V value){
            this.key = key;
            this.value = value;
        }

        @Override
        public K getKey() {
            return key;
        }

        @Override
        public V getValue() {
            return value;
        }

        @Override
        public V setValue(V value) {
            this.value = value;
            return value;
        }

        @SuppressWarnings({"unchecked", "ConstantConditions"})
        @Override
        public int compareTo(ArrayMapEntry o) {
            if(!(key instanceof Comparable) ||
                    !(o instanceof Comparable)){
                return ObjectUtils.compare(hashCode(),o.hashCode(),true);
            }

            return ObjectUtils.compare((Comparable)key,(Comparable)o.getKey(),true);
        }
    }

    public ArrayMap(final Map<K,V> original){
        init(original);
    }

    private void init(final Map<? extends K,? extends V> original){
        ParameterUtils.mustTrueMessage(MapUtils.isNotEmpty(original),"original is empty");
        storager = original.entrySet().stream().map((entry)->
                new ArrayMapEntry<>(entry.getKey(),entry.getValue())).sorted().
                toArray(ArrayMapEntry[]::new);
    }

    @Override
    public int size() {
        return ArrayUtils.getLength(storager);
    }

    @Override
    public boolean isEmpty() {
        return ArrayUtils.isEmpty(storager);
    }

    private int findIndexByKey(K key){
        return Arrays.binarySearch(storager,new ArrayMapEntry<>(key,null));
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean containsKey(Object key) {
        return findIndexByKey((K) key) != -1;
    }

    @Override
    public boolean containsValue(Object value) {
        return Stream.of(storager).filter(v->Objects.equals(v,value)).count() > 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public V get(Object key) {
        int pos = findIndexByKey((K) key);
        if(pos == -1){
            return null;
        }
        return storager[pos].value;
    }

    @Override
    public V put(K key, V value) {
        ParameterUtils.checkSupport(false,"can't be add!");
        return null;
    }

    @Override
    public V remove(Object key) {
        ParameterUtils.checkSupport(false,"can't be deleted!");
        return null;
    }

    @Override
    public void putAll(Map<? extends K, ? extends V> m) {
        //put all
        init(m);
    }

    @Override
    public void clear() {
        storager = null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<K> keySet() {
        Set<K> l = new HashSet<>();
        Stream.of(storager).forEach(v->l.add((K) v.key));
        return l;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<V> values() {
        List<V> l = new ArrayList<>(size());
        Stream.of(storager).forEach(v->l.add((V) v.value));
        return l;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Set<Entry<K, V>> entrySet() {
        return Stream.of(storager).collect(Collectors.toSet());
    }

    public static void main(String[] args){
        Map<String,String> map = new HashMap<>();
        map.put("aaa","1");
        map.put("ccc","2");
        map.put("eee","3");
        map.put("sss","4");
        map.put("bbb","5");
        Map<String,String> newMap = new ArrayMap<>(map);

        System.out.println(newMap.get("sss"));
    }
}
