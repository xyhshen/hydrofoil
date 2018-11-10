package org.hydrofoil.core.engine.internal;

import org.apache.commons.lang.ArrayUtils;
import org.hydrofoil.common.graph.GraphProperty;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.common.util.collect.ArrayMap;
import org.hydrofoil.core.engine.EngineProperty;

import java.util.*;
import java.util.stream.Collectors;

/**
 * EnginePropertySet
 * <p>
 * package org.hydrofoil.core.engine.internal
 *
 * @author xie_yh
 * @date 2018/10/29 19:38
 */
public final class EnginePropertySet {

    public static class Builder{

        private final Map<String,Object> propertyMap;

        public Builder(){
            propertyMap = DataUtils.newMapWithMaxSize(10);
        }

        public void put(String propertyLabel, GraphProperty graphProperty){
            propertyMap.put(propertyLabel,new EngineProperty(propertyLabel,graphProperty));
        }

        public void put(String propertyLabel, Map<String,GraphProperty> subPropertyMap){
            propertyMap.put(propertyLabel,new EngineProperty(propertyLabel,new ArrayMap<>(subPropertyMap)));
        }

        public void puts(String propertyLabel, Collection<GraphProperty> properties){
            propertyMap.put(propertyLabel,properties.stream().
                    map(property -> new EngineProperty(propertyLabel,property)).
                    collect(Collectors.toList()));
        }

        public void putMaps(String propertyLabel, Collection<Map<String,GraphProperty>> subProperties){
            propertyMap.put(propertyLabel,subProperties.stream().
                    map(subPropertyMap -> new EngineProperty(propertyLabel,subPropertyMap)).
                    collect(Collectors.toList()));
        }

        /**
         * @return map
         * @see Builder#toMap()
         **/
        public Map<String, Object> toMap() {
            return Collections.unmodifiableMap(propertyMap);
        }

        /**
         * create set
         * @return property set
         */
        public EnginePropertySet build(){
            return new EnginePropertySet(propertyMap);
        }
    }

    private final Map<String,Object> propertyStoreMap;

    EnginePropertySet(final Map<String,Object> propertyMap){
        propertyStoreMap = new ArrayMap<>(propertyMap);
    }

    /**
     * is multiple property
     * @param propertyLabel property label
     * @return result
     */
    public boolean isMultiple(final String propertyLabel){
        Object collect = propertyStoreMap.get(propertyLabel);
        return collect instanceof List;
    }

    /**
     * get single property
     * @param propertyLabel property label
     * @return property
     */
    @SuppressWarnings("unchecked")
    public EngineProperty get(final String propertyLabel){
        Object property = propertyStoreMap.get(propertyLabel);
        if(property == null){
            return null;
        }
        if(isMultiple(propertyLabel)){
            return DataUtils.lookup((Collection<EngineProperty>)property,0);
        }
        return (EngineProperty) property;
    }

    /**
     * get multi-properties
     * @param propertyLabels property label's
     * @return property list
     */
    @SuppressWarnings("unchecked")
    public Collection<EngineProperty> gets(String ...propertyLabels){
        List<EngineProperty> properties;
        if(ArrayUtils.isEmpty(propertyLabels)){
            propertyLabels = propertyStoreMap.keySet().toArray(new String[propertyStoreMap.size()]);
        }
        properties = new ArrayList<>(propertyLabels.length);
        for(String propertyLabel:propertyLabels){
            Object property = propertyStoreMap.get(propertyLabel);
            if(property == null){
                continue;
            }
            List<EngineProperty> l;
            if(isMultiple(propertyLabel)){
                l = (List<EngineProperty>) property;
            }else{
                l = Collections.singletonList((EngineProperty) property);
            }
            properties.addAll(l);
        }
        return properties;
    }

}
