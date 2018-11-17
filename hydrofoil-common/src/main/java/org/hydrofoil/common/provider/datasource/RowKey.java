package org.hydrofoil.common.provider.datasource;

import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.common.util.collect.ArrayMap;

import java.util.Map;

/**
 * RowKey
 * <p>
 * package org.hydrofoil.common.provider.datasource
 *
 * @author xie_yh
 * @date 2018/11/3 15:29
 */
public final class RowKey {

    private final Map<String,Object> keyValueMap;

    private RowKey(final Map<String,Object> keyValueMap){
        this.keyValueMap = keyValueMap;
    }

    public static RowKey of(final String ...keyvalues){
        final Map<String, Object> rawMap = DataUtils.toMap(keyvalues);
        return new RowKey(new ArrayMap<>(rawMap));
    }

    public static RowKey of(final Map<String,Object> keyValueMap){
        return new RowKey(new ArrayMap<>(keyValueMap));
    }

    /**
     * @return String>
     * @see RowKey#keyValueMap
     **/
    public Map<String, Object> getKeyValueMap() {
        return keyValueMap;
    }
}
