package org.hydrofoil.core.standard;

import org.hydrofoil.common.graph.GraphProperty;
import org.hydrofoil.common.util.ParameterUtils;

import java.util.Map;

/**
 * StandardProperty
 * <p>
 * package org.hydrofoil.core.standard
 *
 * @author xie_yh
 * @date 2018/7/11 17:56
 */
public final class StandardProperty {

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

    public StandardProperty(String label,GraphProperty property){
        this(label,property,false);
    }

    public StandardProperty(String label,Map<String,GraphProperty> propertyMap){
        this(label,propertyMap,true);
    }

    public StandardProperty(String label,Object entity,boolean complex){
        this.label = label;
        this.entity = entity;
        this.complex = complex;
        this.propertyId = label.hashCode() ^ ((long)entity.hashCode() << 32);
    }

    public GraphProperty property(){
        ParameterUtils.mustTrueMessage(!complex,"property not is simple");
        return (GraphProperty) entity;
    }

    @SuppressWarnings("unchecked")
    public Map<String,GraphProperty> properties(){
        ParameterUtils.mustTrueMessage(complex,"property not is simple map");
        return (Map<String, GraphProperty>) entity;
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
     * @see StandardProperty#complex
     **/
    public boolean isComplex() {
        return complex;
    }
}
