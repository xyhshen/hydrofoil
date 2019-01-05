package org.hydrofoil.common.util;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * var
 * <p>
 * package org.hydrofoil.common.util
 *
 * @author xie_yh
 * @date 2019/1/1 8:43
 */
public final class VariableUtils {

    private final static ThreadLocal<Map<String,String>> LOCAL_VARIABLES;

    private final static Pattern SYMBOL_FIND_PATTERN;

    private final static Pattern SYMBOL_VALIDATE_PATTERN;

    static {
        LOCAL_VARIABLES = ThreadLocal.withInitial(Collections::emptyMap);
        SYMBOL_VALIDATE_PATTERN = Pattern.compile("^\\%\\{[\\S]+\\}$");
        SYMBOL_FIND_PATTERN = Pattern.compile("(\\${1}\\{{1})+([^\\{\\}\\s])+(\\}{1})+");
    }

    private static boolean isSymbol(final String str){
        return SYMBOL_VALIDATE_PATTERN.matcher(str).matches();
    }

    public static void setLocal(final Map<String,String> map){
        LOCAL_VARIABLES.set(map);
    }

    public static void clearLocal(){
        LOCAL_VARIABLES.remove();
    }

    public static String getValueByName(final String variableName){
        final Map<String, String> variableMap = LOCAL_VARIABLES.get();
        String value = null;
        if(MapUtils.isNotEmpty(variableMap)){
            value = MapUtils.getString(variableMap,variableName,null);
        }
        if(value == null){
            value = System.getenv(variableName);
        }
        return value;
    }

    public static String getValue(final String variableOrSymbol){
        return getValue(StringUtils.trimToEmpty(variableOrSymbol),variableOrSymbol);
    }

    public static String getValue(final String variableOrSymbol,final String defaultValue){
        String variableName = parseSymbol(variableOrSymbol);
        if(StringUtils.isBlank(variableName)){
            return defaultValue;
        }
        return getValueByName(variableName);
    }

    public static String parseSymbol(final String variableSymbol){
        if(isSymbol(variableSymbol)){
            return null;
        }
        return StringUtils.
                substringBetween(variableSymbol,"${","}");
    }

    public static String parseText(String text){
        final StringBuilder finalyText = new StringBuilder(text);
        Matcher matcher = SYMBOL_FIND_PATTERN.matcher(text);
        while(matcher.find()){
            int start = matcher.start();
            int end = matcher.end();
            String symbol = matcher.group();
            String value = StringUtils.defaultString(getValue(StringUtils.trimToEmpty(symbol),""),"");
            finalyText.replace(start,end,value);
            matcher = SYMBOL_FIND_PATTERN.matcher(finalyText.toString());
        }

        return finalyText.toString();
    }


}
