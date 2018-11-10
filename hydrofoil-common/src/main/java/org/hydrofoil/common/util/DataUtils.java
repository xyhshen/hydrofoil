package org.hydrofoil.common.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hydrofoil.common.util.collect.ArraySet;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
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

    private DataUtils(){}

    @SuppressWarnings("unchecked")
    public static <V> V collectFirst(Collection<V> c){
        return CollectionUtils.isNotEmpty(c)?
                (V) getOptional(c.stream().findFirst()) :null;
    }

    /**
     * get iteraot first value
     * @param iterator iterator
     * @return value
     */
    public static <V> V iteratorFirst(Iterator<V> iterator){
        try{
            return IteratorUtils.get(iterator,0);
        }catch (IndexOutOfBoundsException t){
            return null;
        }
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

    public static <E> List<E> ranger(final List<E> l,Long offset,Long limit){
        long start = offset==null?0:offset;
        long end = limit==null||start + limit>l.size()?l.size() - start:limit;
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

    public static <K, V> HashMap<K, V> newHashMapWithExpectedSize(final int expectedSize) {
        if(expectedSize == 0){
            return new HashMap<K,V>();
        }
        return new HashMap<K, V>(hashMapCapacity(expectedSize));
    }

    public static <K> HashSet<K> newSetMapWithExpectedSize(final int expectedSize) {
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
        return newSetMapWithExpectedSize(maxSize);
    }

    public static int hashMapCapacity(final int expectedSize) {
        if(expectedSize == 0){
            return 0;
        }
        if (expectedSize < 3) {
            ParameterUtils.mustTrue(expectedSize > 0,"expectedSize");
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
        ParameterUtils.mustTrue(length > 2 && (length % 2) == 0);
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

    public static String[] toStringArray(final Collection<String> v){
        if(CollectionUtils.isEmpty(v)){
            return null;
        }
        String[] s = new String[v.size()];
        return v.toArray(s);
    }

    public static <V extends Comparable<? super V>> Set<V> newArraySet(V ...a){
        ParameterUtils.mustTrue(ArrayUtils.isNotEmpty(a));
        return new ArraySet<>(Stream.of(a).collect(Collectors.toSet()));
    }

    public static <T,R> Iterator<R> newIterator(Iterator<T> iterator, Function<T,R> function){
        return new Iterator<R>() {
            @Override
            public boolean hasNext() {
                return iterator.hasNext();
            }

            @Override
            public R next() {
                final T t = iterator.next();
                return function.apply(t);
            }
        };
    }
}
