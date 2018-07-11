package org.hydrofoil.common.util;

import org.apache.commons.collections4.IteratorUtils;

import java.util.Iterator;

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
}
