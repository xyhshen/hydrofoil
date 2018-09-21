package org.hydrofoil.common.util.bean;

import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.Fraction;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.Objects;

/**
 * Beans
 * <p>
 * package org.hydrofoil.common.util.bean
 *
 * @author xie_yh
 * @date 2018/8/30 10:58
 */
public final class Beans {

    /**
     * object to string
     * @param o object
     * @return string value
     */
    public static String toString(final Object o){
        return toString(o,null);
    }

    /**
     * object to string
     * @param o object
     * @param defaultValue default value
     * @return string value
     */
    public static String toString(final Object o,final Object defaultValue){
        String s = Objects.toString(defaultValue,null);
        return Objects.toString(o,s);
    }

    public static Byte toByte(final Object o){
        return toByte(o,null);
    }

    public static Byte toByte(final Object o,final Object b){
        return NumberUtils.toByte(Objects.toString(o,null),b!=null? (byte) b :0);
    }

    public static Character toCharacter(final Object o){
        return toCharacter(o,null);
    }

    public static Character toCharacter(final Object o,final Object c){
        return CharUtils.toChar(Objects.toString(o,null),c!=null? (char) c :0);
    }

    public static Boolean toBoolean(final Object o){
        return toBoolean(o,false);
    }

    public static Boolean toBoolean(final Object o,final Object b){
        if(o == null){
            return (Boolean) b;
        }
        if(o instanceof Number){
            Number n = (Number) o;
            return n.intValue() > 0;
        }
        return BooleanUtils.toBoolean(Objects.toString(o, null));
    }

    public static Long toLong(final Object o){
        return toLong(o,0L);
    }

    public static Long toLong(final Object o,final Object n){
        return NumberUtils.toLong(Objects.toString(o, null), (Long) n);
    }

    public static Double toDouble(final Object o){
        return toDouble(0,0L);
    }

    public static Double toDouble(final Object o,final Object n){
        if(o == null){
            return (Double) n;
        }
        return Fraction.getFraction(Objects.toString(o, null)).doubleValue();
    }

    public static URL toURL(final Object o) {
        return toURL(o,null);
    }

    public static URL toURL(final Object o,final Object u) {
        String s = Objects.toString(o,null);
        if(StringUtils.isBlank(s)){
            return (URL) u;
        }
        try {
            return new URL((String) o);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date toDate(final Object o){
        return toDate(o,null);
    }

    public static Date toDate(final Object o, final Object d){
        if(o instanceof Number){
            return new Date((Long)o);
        }
        String s = Objects.toString(o,null);
        if(StringUtils.isBlank(s)){
            return (Date) d;
        }
        try {
            return DateUtils.parseDate(s,"yyyy-MM-dd'T'HH:mm:ss");
        } catch (ParseException e) {
            e.printStackTrace();
            return (Date) d;
        }
    }

}
