package org.hydrofoil.core.engine;

import org.hydrofoil.common.graph.GraphElementId;
import org.hydrofoil.core.engine.internal.EnginePropertySet;

import java.util.Collection;
import java.util.Objects;

/**
 * StandardElement
 * <p>
 * package org.hydrofoil.core.engine
 *
 * @author xie_yh
 * @date 2018/7/2 15:56
 */
public class EngineElement {

    /**
     * graph id
     */
    private GraphElementId elementId;

    /**
     * property map
     */
    private final EnginePropertySet propertySet;

    EngineElement(GraphElementId elementId, final EnginePropertySet propertySet) {
        this.elementId = elementId;
        this.propertySet = propertySet;
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
     * @see EngineElement#elementId
     **/
    public GraphElementId elementId() {
        return elementId;
    }

    /**
     * @param elementId GraphElementId
     * @see EngineElement#elementId
     **/
    public void elementId(GraphElementId elementId) {
        this.elementId = elementId;
    }

    /**
     * get property
     * @param label property label
     * @return property object
     */
    public EngineProperty property(String label){
        return propertySet.get(label);
    }

    /**
     * get all property
     * @return property list
     */
    public Collection<EngineProperty> properties(String ...labels){
        return propertySet.gets(labels);
    }

    @Override
    public boolean equals(Object obj) {
        if(!(obj instanceof EngineElement)){
            return false;
        }
        EngineElement right = (EngineElement) obj;
        return Objects.equals(right.elementId,this.elementId);
    }

    @Override
    public int hashCode() {
        return elementId.hashCode();
    }
}
