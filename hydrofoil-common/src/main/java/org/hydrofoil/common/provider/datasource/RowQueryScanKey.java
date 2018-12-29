package org.hydrofoil.common.provider.datasource;

import org.apache.commons.lang3.tuple.Pair;
import org.hydrofoil.common.util.bean.KeyValueEntity;

import java.util.*;

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

    public Collection<RowStore> filter(final Collection<RowStore> rows){
        List<RowStore> l = new ArrayList<>(rows.size());
        for(RowStore row:rows){
            List<Pair<String,Object>> values = new ArrayList<>();
            for(String key:keyFactory.keys()){
                values.add(Pair.of(key,row.value(name,key)));
            }
            for(KeyValueEntity keyValue:keyValueEntities){
                boolean found = false;
                final Map<String, Object> keyValueMap = keyValue.getKeyValueMap();
                for(Pair<String,Object> pair:values){
                    Object value = keyValueMap.get(pair.getLeft());
                    found = Objects.equals(value,pair.getLeft());
                    if(!found){
                        break;
                    }
                }
                if(found){
                    l.add(row);
                    break;
                }
            }
        }
        return l;
    }
}
