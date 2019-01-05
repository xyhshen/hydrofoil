package org.hydrofoil.core.engine;

import org.hydrofoil.common.graph.GraphProperty;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.common.util.ArgumentUtils;

import java.util.Map;

/**
 * EngineProperty
 * <p>
 * package org.hydrofoil.core.engine
 *
 * @author xie_yh
 * @date 2018/7/11 17:56
 */
public final class EngineProperty {

    /**
     * property id
     */
    private Long propertyId;

    /**
     * label
     */
    private String label;

    /**
     * property entity
     */
    private Object entity;

    /**
     * is complex property,property wrap item is map
     */
    private boolean complex;

    public EngineProperty(String label, GraphProperty property){
        this(label,property,false);
    }

    public EngineProperty(String label, Map<String,GraphProperty> propertyMap){
        this(label,propertyMap,true);
    }

    public EngineProperty(String label, Object entity, boolean complex){
        this.label = label;
        this.entity = entity;
        this.complex = complex;
        this.propertyId = label.hashCode() ^ ((long)entity.hashCode() << 32);
    }

    public GraphProperty property(){
        ArgumentUtils.mustTrueMessage(!complex,"property not is simple");
        return (GraphProperty) entity;
    }

    @SuppressWarnings("unchecked")
    public Map<String,GraphProperty> properties(){
        ArgumentUtils.mustTrueMessage(complex,"property not is simple map");
        return (Map<String, GraphProperty>) entity;
    }

    public Map<String,Object> asValueMap(){
        ArgumentUtils.mustTrueMessage(complex,"must is complex");
        Map<String,Object> map = DataUtils.newMapWithMaxSize(0);
        properties().forEach((k,v)->{
            map.put(k,v.content());
        });
        return map;
    }

    /**
     * property id
     * @return id
     */
    public Long id(){
        return propertyId;
    }

    /**
     * property label
     * @return label
     */
    public String label(){
        return label;
    }

    /**
     * @return $field.TypeName
     * @see EngineProperty#complex
     **/
    public boolean isComplex() {
        return complex;
    }
}
