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
     *
     */
    private Object graphProperty;

    /**
     * is complex property,property wrap item is map
     */
    private boolean complex;

    /**
     * will exist multi item
     */
    private boolean multiple;

    public StandardProperty(String label,Object graphProperty,boolean complex,boolean multiple){
        this.label = label;
        this.graphProperty = graphProperty;
        this.complex = complex;
        this.multiple = multiple;
    }

    public GraphProperty simple(){
        ParameterUtils.mustTrueMessage(!complex && !multiple,"property not is simple");
        return (GraphProperty) graphProperty;
    }

    @SuppressWarnings("unchecked")
    public Map<String,GraphProperty> map(){
        ParameterUtils.mustTrueMessage(complex && !multiple,"property not is simple map");
        return (Map<String, GraphProperty>) graphProperty;
    }

    public GraphProperty[] list(){
        ParameterUtils.mustTrueMessage(!complex && multiple,"property not is simple list");
        return (GraphProperty[]) graphProperty;
    }

    @SuppressWarnings("unchecked")
    public Map<String,GraphProperty>[] flatMap(){
        ParameterUtils.mustTrueMessage(!complex && multiple,"property not is simple list");
        return (Map<String, GraphProperty>[]) graphProperty;
    }

    public Long id(){
        return propertyId;
    }

    public String label(){
        return label;
    }

}
