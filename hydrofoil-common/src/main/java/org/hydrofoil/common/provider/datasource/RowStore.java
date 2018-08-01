package org.hydrofoil.common.provider.datasource;

import org.apache.commons.collections4.MapUtils;
import org.hydrofoil.common.util.bean.FieldPair;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

/**
 * RowStore
 * <p>
 * package org.hydrofoil.common.provider.datasource
 *
 * @author xie_yh
 * @date 2018/7/4 10:41
 */
public final class RowStore {

    private Map<String,Map<String,FieldPair>> dataSet;

    public RowStore(){
        this.dataSet = new TreeMap<>();
    }

    /**
     * put field
     * @param name collect set name
     * @param pair field pair
     * @return this
     */
    public RowStore put(String name,FieldPair pair){
        Map<String, FieldPair> fieldPairMap = dataSet.computeIfAbsent(name, (k) -> new HashMap<>());
        fieldPairMap.put(pair.name(),pair);
        return this;
    }

    /**
     * put a field value
     * @param name collect set name
     * @param fieldname field name
     * @param value field value
     * @return this
     */
    public RowStore put(String name,String fieldname,Object value){
        return put(name,new FieldPair(fieldname,value));
    }

    /**
     * get field
     * @param name field
     * @param fieldname fieldname
     * @return field pair
     */
    public FieldPair field(String name,String fieldname){
        Map<String, FieldPair> fieldPairMap = dataSet.get(name);
        if(fieldPairMap == null){
            return null;
        }
        return fieldPairMap.get(fieldname);
    }

    /**
     *
     * @param name collect set name
     * @param fieldname field name
     * @return value
     */
    public Object value(String name,String fieldname){
        FieldPair field = field(name, fieldname);
        return field!=null?field.first():null;
    }

}
