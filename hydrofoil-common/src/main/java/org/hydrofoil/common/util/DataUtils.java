package org.hydrofoil.common.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.*;

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

    public static Object getOptional(final Optional<?> optional){
        if(optional == null){
            return null;
        }
        return optional.orElse(null);
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
        if(maxSize <= 50){
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
}
