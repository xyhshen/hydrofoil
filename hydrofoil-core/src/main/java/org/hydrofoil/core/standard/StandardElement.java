package org.hydrofoil.core.standard;

import org.hydrofoil.common.graph.GraphElementId;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * StandardElement
 * <p>
 * package org.hydrofoil.core.standard
 *
 * @author xie_yh
 * @date 2018/7/2 15:56
 */
public class StandardElement {

    /**
     * graph id
     */
    private GraphElementId elementId;

    /**
     * property map
     */
    private Map<String,StandardProperty> propertyMap;

    StandardElement(GraphElementId elementId) {
        this(elementId,new HashMap<>());
    }

    StandardElement(GraphElementId elementId,Map<String,StandardProperty> propertyMap) {
        this.elementId = elementId;
        this.propertyMap = propertyMap;
    }

    /**
     * get label
     * @return label
     */
    public String label(){
        return elementId.label();
    }

    /**
     * @return GraphElementId
     * @see StandardElement#elementId
     **/
    public GraphElementId elementId() {
        return elementId;
    }

    /**
     * @param elementId GraphElementId
     * @see StandardElement#elementId
     **/
    public void elementId(GraphElementId elementId) {
        this.elementId = elementId;
    }

    /**
     * get property
     * @param label property label
     * @return property object
     */
    public StandardProperty property(String label){
        return propertyMap.get(label);
    }

    /**
     * get all property
     * @return property list
     */
    public Collection<StandardProperty> properties(){
        return propertyMap.values();
    }
}
