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

    public StandardProperty(String label,Object graphProperty,boolean complex){
        this.label = label;
        this.graphProperty = graphProperty;
        this.complex = complex;
    }

    public GraphProperty simple(){
        ParameterUtils.mustTrueMessage(!complex,"property not is simple");
        return (GraphProperty) graphProperty;
    }

    @SuppressWarnings("unchecked")
    public Map<String,GraphProperty> map(){
        ParameterUtils.mustTrueMessage(complex,"property not is simple map");
        return (Map<String, GraphProperty>) graphProperty;
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
