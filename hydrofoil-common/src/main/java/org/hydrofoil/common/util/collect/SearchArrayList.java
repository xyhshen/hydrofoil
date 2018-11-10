package org.hydrofoil.common.util.collect;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MultiMapUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.lang3.tuple.Pair;
import org.hydrofoil.common.util.Algorithms;
import org.hydrofoil.common.util.ParameterUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * SearchArrayList
 * <p>
 * package org.hydrofoil.common.util.collect
 *
 * @author xie_yh
 * @date 2018/11/5 19:10
 */
public class SearchArrayList<K extends Comparable<? super K>,V> {

    private Object[] keys;

    private MultiValuedMap<K,V> valuedMap;

    private boolean sorted;

    public SearchArrayList(){
        valuedMap = MultiMapUtils.newListValuedHashMap();
    }

    public void add(final K key,final V value){
        valuedMap.put(key,value);
        sorted = false;
    }

    public void sorting(){
        keys = new Object[valuedMap.keySet().size()];
        valuedMap.keySet().toArray(keys);
        Arrays.sort(keys);
        sorted = true;
    }

    public boolean isSorted(){
        return sorted;
    }

    @SuppressWarnings("unchecked")
    public Collection<V> getRange(final K start, final K end){
        ParameterUtils.mustTrue(start != null);
        if(!isSorted()){
            sorting();
        }

        final Pair<Integer, Integer> result = Algorithms.
                searchRanger(keys, start, end);
        if(result == null || (result.getRight() - result.getLeft()) <= 0){
            return null;
        }
        int size = result.getRight() - result.getLeft();
        List<V> l = new ArrayList<>(size);
        for(int i = result.getLeft();i <= result.getRight();i++){
            Collection<V> a = valuedMap.get((K) keys[i]);
            l.addAll(a);
        }
        return ListUtils.unmodifiableList(l);
    }

    public Collection<V> getRange(final K start){
        return getRange(start,null);
    }

    @SuppressWarnings("unchecked")
    public Collection<V> find(final K key){
        return valuedMap.get(key);
    }

}
