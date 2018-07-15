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
     *
     * @param o
     * @param message
     */
    public static void nullMessage(final Object o,final String message){
        if(o == null){
            throw new IllegalArgumentException(message);
        }
    }

    /**
     * must is true
     * @param b value
     */
    public static void mustTrue(final boolean b){
        mustTrue(b,"parameter");
    }

    /**
     * must is true
     * @param b value
     * @param name name
     */
    public static void mustTrue(final boolean b,final String name){
        if(!b){
            throw new IllegalArgumentException(name + " is false!");
        }
    }

    public static void mustTrueMessage(final boolean b,final String message){
        if(!b){
            throw new IllegalArgumentException(message);
        }
    }

    /**
     *
     * @param b
     * @param message
     * @param t
     */
    public static void mustTrueException(final boolean b,final String message,Throwable t){
        if(!b){
            throw new RuntimeException(message,t);
        }
    }

}
