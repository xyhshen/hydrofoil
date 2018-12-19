package org.hydrofoil.common.provider.datasource;

import org.hydrofoil.common.util.bean.KeyValueEntity;

import java.util.Collection;

/**
 * RowQueryScanKey
 * <p>
 * package org.hydrofoil.common.provider.datasource
 *
 * @author xie_yh
 * @date 2018/12/19 18:47
 */
public final class RowQueryScanKey {

    /**
     * collect name
     */
    private final String name;

    private final KeyValueEntity.KeyValueEntityFactory keyFactory;

    private final Collection<KeyValueEntity> keyValueEntities;

    public RowQueryScanKey(
            final String name,
            final KeyValueEntity.KeyValueEntityFactory keyFactory,
            final Collection<KeyValueEntity> keyValueEntities){
        this.name = name;
        this.keyFactory = keyFactory;
        this.keyValueEntities = keyValueEntities;
    }

    public Collection<String> getKeys(){
        return keyFactory.keys();
    }


    /**
     * @return String
     * @see RowQueryScanKey#name
     **/
    public String getName() {
        return name;
    }

    /**
     * @return KeyValueEntity>
     * @see RowQueryScanKey#keyValueEntities
     **/
    public Collection<KeyValueEntity> getKeyValueEntities() {
        return keyValueEntities;
    }
}
