package org.hydrofoil.common.util;

import org.apache.commons.lang3.StringUtils;

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
     * not blank verify
     * @param s string
     */
    public static void notBlank(final String s){
        if(StringUtils.isBlank(s)){
            throw new IllegalArgumentException();
        }
    }

}
