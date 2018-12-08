package org.hydrofoil.common.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateUtils;

import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.util.Date;
import java.util.Set;

/**
 * EncodeUtils
 * <p>
 * package org.hydrofoil.common.util
 *
 * @author xie_yh
 * @date 2018/7/27 16:20
 */
public final class EncodeUtils {

    /**
     * get base 64 string
     * @param str string
     * @return base64 string
     */
    public static String base64(String str){
        try {
            if(StringUtils.isBlank(str)){
                return "";
            }
            return new String(Base64.encodeBase64(str.getBytes("UTF-8")));
        } catch (Throwable t) {
            return "";
        }
    }

    public static String base64Decode(String bs){
        try {
            if(StringUtils.isBlank(bs)){
                return "";
            }
            byte[] bp = Base64.decodeBase64(bs.getBytes("UTF-8"));
            return new String(bp,"UTF-8");
        } catch (Exception e) {
            return "";
        }
    }

    public static String charsetEncode(final String s,final String charsetName){
        if(StringUtils.isEmpty(s)){
            return null;
        }
        try {
            return new String(s.getBytes(charsetName));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Date parseDate(final String s, final String pattern){
        try {
            return DateUtils.parseDate(s,pattern);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Set<String> splitText(final String s){
        final String bs = StringUtils.normalizeSpace(s);
        final String[] strs = bs.split("[,._\\*\\/\\s]+");
        Set<String> set = DataUtils.newHashSetWithExpectedSize(0);
        for(String ss:strs){
            if(StringUtils.isNotBlank(ss)){
                set.add(ss);
            }
        }
        return set;
    }

    public static String wrapString(final String raw,final String wrapChars,final char ...checkChars){
        if(ArrayUtils.isNotEmpty(checkChars)){
            if(!StringUtils.containsAny(raw,checkChars)){
                return raw;
            }
        }
        return StringUtils.wrap(raw,wrapChars);
    }

    public static String unWrapString(final String raw,final String wrapChars){
        if(!StringUtils.startsWith(raw,wrapChars) ||
                !StringUtils.endsWith(raw,wrapChars)){
            return raw;
        }

        return StringUtils.unwrap(raw,wrapChars);
    }

}
