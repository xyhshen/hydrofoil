package org.hydrofoil.common.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hydrofoil.common.util.collect.ArraySet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * DataUtils
 * <p>
 * package org.hydrofoil.common.util
 *
 * @author xie_yh
 * @date 2018/7/10 13:34
 */
public final class DataUtils {

    private static final int MAX_POWER_OF_TWO = 1 << (Integer.SIZE - 2);

    private static final int USE_HASH_MAP_MAX_SIZE = 50;

    private static final int DEFAULT_SIZE = 10;

    private DataUtils(){}

    @SuppressWarnings("unchecked")
    public static <V> V collectFirst(final Collection<V> c){
        return CollectionUtils.isNotEmpty(c)?
                (V) getOptional(c.stream().findFirst()) :null;
    }

    /**
     * get iteraot first value
     * @param iterator iterator
     * @return value
     */
    public static <V> V iteratorFirst(final Iterator<V> iterator){
        try{
            return IteratorUtils.get(iterator,0);
        }catch (IndexOutOfBoundsException t){
            return null;
        }
    }

    public static <K,V> V mapFirst(final Map<K,V> map){
        final Map.Entry<K, V> entry = CollectionUtils.get(map, 0);
        if(entry == null){
            return null;
        }
        return entry.getValue();
    }

    /**
     * open file by path,from local resource or file
     * @param path path
     * @return stream
     * @throws IOException
     */
    public static InputStream openFile(String path) {
        InputStream resourceAsStream = Thread.currentThread().
                getContextClassLoader().getResourceAsStream(path);
        if(resourceAsStream != null){
            return resourceAsStream;
        }
        try {
            return FileUtils.openInputStream(new File(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static boolean isAllBlank(String ...s){
        if(ArrayUtils.isEmpty(s)){
            return true;
        }
        for(String str:s){
            if(StringUtils.isNotBlank(str)){
                return false;
            }
        }
        return true;
    }

    public static Object getOptional(final Optional<?> optional){
        if(optional == null){
            return null;
        }
        return optional.orElse(null);
    }

    public static <E> List<E> range(final List<E> l, Long offset, Long limit){
        long start = offset==null?0:offset;
        long end = limit==null||start + limit>l.size()?l.size() - start:limit;
        if(start == 0 && end == l.size()){
            return l;
        }
        return l.subList((int)start,(int)end);
    }

    public static <V> V lookup(V[] l,int index){
        return lookup(l,index,null);
    }

    public static <V> V lookup(V[] l,int index,V defaultValue){
        int length = ArrayUtils.getLength(l);
        if(length <= 0){
            return defaultValue;
        }
        if(index == -1){
            return l[length - 1];
        }
        return index < length?l[index]:defaultValue;
    }

    public static <V> V lookup(Collection<V> c,int index){
        return lookup(c,index,null);
    }

    public static <V> V lookup(Collection<V> c,int index,V defaultValue){
        int length = CollectionUtils.size(c);
        if(length <= 0){
            return defaultValue;
        }
        if(index == -1){
            return IterableUtils.get(c,length - 1);
        }
        return index < length?IterableUtils.get(c,index):defaultValue;
    }

    public static <K, V> HashMap<K, V> newHashMapWithExpectedSize() {
        return newHashMapWithExpectedSize(DEFAULT_SIZE);
    }

    public static <K, V> HashMap<K, V> newHashMapWithExpectedSize(final int expectedSize) {
        if(expectedSize == 0){
            return new HashMap<K,V>();
        }
        return new HashMap<K, V>(hashMapCapacity(expectedSize));
    }

    public static <K> HashSet<K> newHashSetWithExpectedSize() {
        return newHashSetWithExpectedSize(DEFAULT_SIZE);
    }

    public static <K> HashSet<K> newHashSetWithExpectedSize(final int expectedSize) {
        if(expectedSize == 0){
            return new HashSet<K>();
        }
        return new HashSet<K>(hashMapCapacity(expectedSize));
    }

    public static <K,V> Map<K,V> newMapWithMaxSize(final int maxSize){
        if(maxSize <= USE_HASH_MAP_MAX_SIZE){
            return new TreeMap<K,V>();
        }
        return newHashMapWithExpectedSize(maxSize);
    }

    public static <K> Set<K> newSetWithMaxSize(final int maxSize){
        if(maxSize <= USE_HASH_MAP_MAX_SIZE){
            return new TreeSet<K>();
        }
        return newHashSetWithExpectedSize(maxSize);
    }

    public static <K,V> MultiValuedMap<K,V> newMultiMapWithMaxSize(final int expectedSize){
        return newMultiMapWithMaxSize(expectedSize,0);
    }

    public static <K,V> MultiValuedMap<K,V> newMultiMapWithMaxSize(final int expectedSize,final int listSize){
        if(expectedSize == 0){
            return new ArrayListValuedHashMap<>();
        }
        return new ArrayListValuedHashMap<K, V>(hashMapCapacity(expectedSize),listSize);
    }

    public static int hashMapCapacity(final int expectedSize) {
        if(expectedSize == 0){
            return 0;
        }
        if (expectedSize < 3) {
            ArgumentUtils.mustTrue(expectedSize > 0,"expectedSize");
            return expectedSize + 1;
        }
        if (expectedSize < MAX_POWER_OF_TWO) {   //MAX_POWER_OF_TWO = 1 << (Integer.SIZE - 2); //Integer.SIZE = 32;
            // This is the calculation used in JDK8 to resize when a putAll
            // happens; it seems to be the most conservative calculation we
            // can make.  0.75 is the default load factor.
            return (int) ((float) expectedSize / 0.75F + 1.0F);
        }
        return Integer.MAX_VALUE; // any large value //MAX_VALUE = 0x7fffffff;
    }

    @SuppressWarnings("unchecked")
    public static <K,V> Map<K,V> toMap(Object ...keyvalues){
        int length = ArrayUtils.getLength(keyvalues);
        ArgumentUtils.mustTrue(length > 2 && (length % 2) == 0);
        Map<K,V> map = DataUtils.newMapWithMaxSize(length / 2);
        for(int i = 0;i < length / 2;i++){
            map.put((K)keyvalues[i],(V)keyvalues[i * 2 + 1]);
        }
        return map;
    }

    /**
     * create new list
     * @return list
     */
    public static <V> List<V> newList(){
        return new ArrayList<V>();
    }

    public static <E> Iterator<E> newCountIterator(final int count,final E empty){
        class CountIterator<E> implements Iterator<E>{

            private final long total;

            private long current;

            private final E empty;

            private CountIterator(final long count, final E empty){
                this.total = count;
                this.empty = empty;
                this.current = 0;
            }

            @Override
            public boolean hasNext() {
                return current < total;
            }

            @Override
            public E next() {
                current++;
                return empty;
            }
        }
        return new CountIterator<>(count,empty);
    }

    public static String[] toStringArray(final Collection<String> v){
        return toArray(v,String.class);
    }

    @SuppressWarnings("unchecked")
    public static <E> E[] toArray(Collection<E> c,Class<E> clz){
        if(CollectionUtils.isEmpty(c)){
            return null;
        }
        E[] o = (E[]) Array.newInstance(clz,c.size());
        return c.toArray(o);
    }

    public static <V extends Comparable<? super V>> Set<V> newArraySet(V ...a){
        ArgumentUtils.mustTrue(ArrayUtils.isNotEmpty(a));
        return new ArraySet<>(Stream.of(a).collect(Collectors.toSet()));
    }

    @SuppressWarnings("unchecked")
    public static <T,R> Iterator<R> newIterator(final Iterator<T> iterator, final Function<T,R> function){
        return new Iterator<R>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @SuppressWarnings("unchecked")
            @Override
            public R next() {
                T t = iterator.next();
                return function.apply(t);
            }
        };
    }

    public static <A,B extends A> Collection<B> castCollectType(final Collection<A> c){
        return c.stream().map(v->(B)v).collect(Collectors.toList());
    }

}