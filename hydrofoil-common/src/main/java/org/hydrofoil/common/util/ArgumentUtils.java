package org.hydrofoil.common.util;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collection;

/**
 * Parameter transform and verify
 * <p>
 * package org.hydrofoil.common.util
 *
 * @author xie_yh
 * @date 2018/6/30 11:07
 */
public final class ArgumentUtils {


    /**
     * string not blank verify
     * @param s string
     * @return s
     */
    public static String notBlank(final String s){
        return notBlank(s,"parameter");
    }

    /**
     * string not blank verify
     * @param s string
     * @param name name
     * @return s
     */
    public static String notBlank(final String s,final String name){
        if(StringUtils.isBlank(s)){
            throw new IllegalArgumentException(name + " is blank!");
        }
        return s;
    }

    /**
     * collection not empty
     * @param c collection
     */
    public static Collection notEmpty(final Collection c){
        return notEmpty(c,"parameter");
    }

    /**
     * collection not empty
     * @param c collection
     * @param name name
     */
    public static Collection notEmpty(final Collection c,final String name){
        if(CollectionUtils.isEmpty(c)){
            throw new IllegalArgumentException(name + " is empty!");
        }
        return c;
    }

    /**
     * object not is null
     * @param o object
     */
    public static Object notNull(final Object o){
        return notNull(o,"parameter");
    }

    /**
     * object not is null
     * @param o object
     * @param name name
     */
    public static Object notNull(final Object o,final String name){
        if(o == null){
            throw new IllegalArgumentException(name + " is null!");
        }
        return o;
    }

    /**
     *
     * @param o
     * @param message
     */
    public static Object notNullMessage(final Object o,final String message){
        if(o == null){
            throw new IllegalArgumentException(message);
        }
        return o;
    }

    /**
     * must is true
     * @param b value
     */
    public static boolean mustTrue(final boolean b){
        return mustTrue(b,"parameter");
    }

    /**
     * must is true
     * @param b value
     * @param name name
     */
    public static boolean mustTrue(final boolean b,final String name){
        if(!b){
            throw new IllegalArgumentException(name + " is false!");
        }
        return true;
    }

    /**
     *
     * @param b
     * @param message
     * @return
     */
    public static boolean mustTrueMessage(final boolean b,final String message){
        if(!b) {
            throw new IllegalArgumentException(message);
        }
        return true;
    }

    /**
     *
     * @param b
     * @param message
     * @param t
     */
    public static boolean mustTrueException(final boolean b,final String message,Throwable t){
        if(!b){
            throw new RuntimeException(message,t);
        }
        return true;
    }

    public static void checkStateMessage(final boolean b,final String message){
        if(!b){
            throw new IllegalStateException(message);
        }
    }

    public static void checkSupport(final boolean b,final String message){
        if(!b){
            throw new UnsupportedOperationException(message);
        }
    }

    public static void checkValueIn(final String checkValue,final String ...values){
        if(!StringUtils.equalsAnyIgnoreCase(checkValue,values)){
            throw new IllegalArgumentException(checkValue + "value not in set");
        }
    }

    public static void checkValueNullable(final String checkValue,final String ...values){
        if(StringUtils.isBlank(checkValue)){
            return;
        }
        checkValueIn(checkValue,values);
    }

}
