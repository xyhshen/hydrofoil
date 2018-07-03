package org.hydrofoil.common.util;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.hydrofoil.common.util.bean.FieldPair;

import java.util.*;

/**
 * FieldUtils
 * <p>
 * package org.hydrofoil.common.util
 *
 * @author xie_yh
 * @date 2018/7/3 15:41
 */
public final class FieldUtils {

    /**
     * xxx=yyy;xxx1=yyy1 to map
     * @param field format
     * @return map
     */
    public static Map<String,String> toMap(final String field){
        Map<String,String> map = new TreeMap<>();
        Collection<FieldPair> fieldPairs = toPair(field);
        fieldPairs.forEach((v)->{map.put(v.name(),Objects.toString(v.first()));});
        return map;
    }

    /**
     * xxx=yyy;xxx1=yyy1 to pairs
     * @param field format
     * @return pairs
     */
    @SuppressWarnings("unchecked")
    public static Collection<FieldPair> toPair(final String field){
        String[] fields = StringUtils.split(field, ";");
        if(ArrayUtils.isEmpty(fields)){
            return Collections.EMPTY_LIST;
        }
        List<FieldPair> fieldPairs = new ArrayList<>(fields.length);
        for(String f:fields){
            String[] raw = StringUtils.split(f, "=");
            if(ArrayUtils.getLength(raw) < 2){
                continue;
            }

            ParameterUtils.notBlank(raw[0]);
            ParameterUtils.notBlank(raw[1]);

            fieldPairs.add(new FieldPair(raw[0],raw[1]));
        }
        return fieldPairs;
    }

}
