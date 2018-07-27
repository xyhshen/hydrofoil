package org.hydrofoil.common.util;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Optional;

/**
 * DataUtils
 * <p>
 * package org.hydrofoil.common.util
 *
 * @author xie_yh
 * @date 2018/7/10 13:34
 */
public final class DataUtils {

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

    public static Object getOptional(Optional<?> optional){
        if(optional == null){
            return null;
        }
        return optional.orElse(null);
    }
}
