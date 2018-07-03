package org.hydrofoil.common.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

/**
 * AssetUtils
 * <p>
 * package org.hydrofoil.common.util
 *
 * @author xie_yh
 * @date 2018/6/30 11:07
 */
public final class ParameterUtils {

    /**
     * string not blank verify
     * @param s string
     */
    public static void notBlank(final String s){
        if(StringUtils.isBlank(s)){
            throw new IllegalArgumentException("parameter is blank!");
        }
    }

    /**
     * collection not empty
     * @param c collection
     */
    public static void notEmpty(final Collection c){
        if(CollectionUtils.isEmpty(c)){
            throw new IllegalArgumentException("parameter is blank!");
        }
    }

    /**
     * object not is null
     * @param o object
     */
    public static void notNull(final Object o){
        if(o == null){
            throw new IllegalArgumentException("parameter is null!");
        }
    }

    /**
     * must is true
     * @param b value
     */
    public static void mustTrue(boolean b){
        if(!b){
            throw new IllegalArgumentException("parameter is null!");
        }
    }

}
