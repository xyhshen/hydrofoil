package org.hydrofoil.common.util.collect;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.hydrofoil.common.util.ParameterUtils;

import java.io.Serializable;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hydrofoil.common.util.ParameterUtils.checkSupport;

/**
 * ArrayMap
 * <p>
 * package org.hydrofoil.common.util.collect
 *
 * @author xie_yh
 * @date 2018/7/30 18:54
 */
public class ArrayMap<K extends Comparable<? super K>,V> implements Map<K,V>, Cloneable, Serializable {

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

        @SuppressWarnings({"unchecked"})
        @Override
        public int compareTo(ArrayMapEntry o) {
            if(o == null){
                return -1;
            }
            if(!(key instanceof Comparable) ||
                    !(o.key instanceof Comparable)){
                return ObjectUtils.compare(Objects.hashCode(key),Objects.hashCode(o.key),true);
            }

            return ObjectUtils.compare((Comparable)key,(Comparable)o.getKey(),true);
        }

        @Override
        public int hashCode(){
            return Objects.hashCode(key) ^ Objects.hashCode(value);
        }

        @SuppressWarnings("unchecked")
        @Override
        public boolean equals(Object otherObj){
            return (otherObj instanceof ArrayMapEntry) &&
                    Objects.equals(((ArrayMapEntry)(otherObj)).key,this.key) &&
                        Objects.equals(((ArrayMapEntry)(otherObj)).value,this.value);
        }
    }

    @SuppressWarnings("unchecked")
    public ArrayMap(final K key, final V value){
        storager = new ArrayMapEntry[1];
        storager[1] = new ArrayMapEntry<>(key,value);
    }

    public ArrayMap(final Map<K,V> original){
        init(original);
    }

    @SuppressWarnings("unchecked")
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

    private int findIndexByKey(final K key){
        if(isEmpty() || key == null){
            return -1;
        }
        return Arrays.binarySearch(storager,new ArrayMapEntry<>(key,null));
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean containsKey(Object key) {
        return findIndexByKey((K) key) != -1;
    }

    @Override
    public boolean containsValue(Object value) {
        return !isEmpty() && Stream.of(storager).filter(v -> Objects.equals(v, value)).count() > 0;
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
        checkSupport(false,"can't be add!");
        return null;
    }

    @Override
    public V remove(Object key) {
        checkSupport(false,"can't be deleted!");
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
        Set<K> s = new HashSet<>();
        Stream.of(storager).forEach(v->s.add((K) v.key));
        return s;
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

    @Override
    public Object clone(){
        try {
            ArrayMap map = (ArrayMap) super.clone();
            if(!isEmpty()){
                map.storager = Arrays.copyOf(storager,size());
            }
            return map;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int hashCode(){
        return Objects.hash((Object[]) storager);
    }

    @Override
    public boolean equals(Object otherObj){
        if(!(otherObj instanceof ArrayMap)){
            return false;
        }
        return Arrays.equals(((ArrayMap)otherObj).storager,storager);
    }
}
