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
        notBlank(s,"parameter");
    }

    /**
     * string not blank verify
     * @param s string
     * @param name name
     */
    public static void notBlank(final String s,final String name){
        if(StringUtils.isBlank(s)){
            throw new IllegalArgumentException(name + " is blank!");
        }
    }

    /**
     * collection not empty
     * @param c collection
     */
    public static void notEmpty(final Collection c){
        notEmpty(c,"parameter");
    }

    /**
     * collection not empty
     * @param c collection
     * @param name name
     */
    public static void notEmpty(final Collection c,final String name){
        if(CollectionUtils.isEmpty(c)){
            throw new IllegalArgumentException(name + " is empty!");
        }
    }

    /**
     * object not is null
     * @param o object
     */
    public static void notNull(final Object o){
        notNull(o,"parameter");
    }

    /**
     * object not is null
     * @param o object
     * @param name name
     */
    public static void notNull(final Object o,final String name){
        if(o == null){
            throw new IllegalArgumentException(name + " is null!");
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
