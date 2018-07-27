package org.hydrofoil.common.util;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;

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

}
