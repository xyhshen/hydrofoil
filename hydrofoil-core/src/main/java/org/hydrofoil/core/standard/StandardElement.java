package org.hydrofoil.core.standard;

import org.hydrofoil.common.graph.GraphElementId;

/**
 * StandardElement
 * <p>
 * package org.hydrofoil.core.standard
 *
 * @author xie_yh
 * @date 2018/7/2 15:56
 */
class StandardElement {

    /**
     * graph id
     */
    private GraphElementId elementId;

    StandardElement(GraphElementId elementId){
        this.elementId = elementId;
    }

    /**
     * @return GraphElementId
     * @see StandardElement#elementId
     **/
    public GraphElementId getElementId() {
        return elementId;
    }

    /**
     * @param elementId GraphElementId
     * @see StandardElement#elementId
     **/
    public void setElementId(GraphElementId elementId) {
        this.elementId = elementId;
    }
}
