package org.hydrofoil.provider.sequence;

import org.apache.commons.collections4.MapUtils;

import java.util.Map;

/**
 * FileRow
 * <p>
 * package org.hydrofoil.provider.sequence
 *
 * @author xie_yh
 * @date 2018/11/9 15:29
 */
public final class FileRow {

    private final String[] values;

    private final Map<String,Integer> header;

    public FileRow(final String[] values, final Map<String, Integer> header) {
        this.values = values;
        this.header = header;
    }

    public String value(final String fieldname){
        Integer i = MapUtils.getInteger(header,fieldname);
        return values[i];
    }

    public String[] values(){
        return values;
    }
}
