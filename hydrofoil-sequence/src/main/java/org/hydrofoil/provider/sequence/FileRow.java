package org.hydrofoil.provider.sequence;

import org.apache.commons.collections4.MapUtils;
import org.hydrofoil.common.util.ParameterUtils;

import java.util.Map;

/**
 * FileRow
 * <p>
 * package org.hydrofoil.provider.sequence
 *
 * @author xie_yh
 * @date 2018/11/9 15:29
 */
final class FileRow {

    private final Object[] values;

    private final Map<String,Integer> header;

    FileRow(final Object[] values, final Map<String, Integer> header) {
        this.values = values;
        this.header = header;
    }

    Object value(final String fieldname){
        Integer i = MapUtils.getInteger(header,fieldname);
        ParameterUtils.notNull(i);
        return values[i];
    }

    Object[] values(){
        return values;
    }
}
