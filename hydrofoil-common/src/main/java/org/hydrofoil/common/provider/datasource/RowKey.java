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

    private final Map<String,String> keyValueMap;

    private RowKey(final Map<String,String> keyValueMap){
        this.keyValueMap = keyValueMap;
    }

    public static RowKey of(String ...keyvalues){
        final Map<String, String> rawMap = DataUtils.toMap(keyvalues);
        RowKey rowKey = new RowKey(new ArrayMap<>(rawMap));
        return rowKey;
    }

    /**
     * @return String>
     * @see RowKey#keyValueMap
     **/
    public Map<String, String> getKeyValueMap() {
        return keyValueMap;
    }
}
