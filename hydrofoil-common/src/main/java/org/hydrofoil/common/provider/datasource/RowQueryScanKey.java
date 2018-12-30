package org.hydrofoil.common.provider.datasource;

import org.hydrofoil.common.util.bean.KeyValueEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

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

    private final Set<KeyValueEntity> keyValueEntities;

    public RowQueryScanKey(
            final String name,
            final KeyValueEntity.KeyValueEntityFactory keyFactory,
            final Set<KeyValueEntity> keyValueEntities){
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

    public Collection<RowStore> filter(final Collection<RowStore> rows){
        List<RowStore> l = new ArrayList<>(rows.size());
        for(RowStore row:rows){
            final KeyValueEntity keyValue = keyFactory.create();
            for(String key:keyFactory.keys()){
                keyValue.put(key,row.value(name,key));
            }
            if(keyValueEntities.contains(keyValue)){
                l.add(row);
            }
        }
        return l;
    }
}
