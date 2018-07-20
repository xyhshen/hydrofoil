package org.hydrofoil.core.standard;

import org.hydrofoil.common.graph.GraphElementId;
import org.hydrofoil.common.graph.GraphVertexId;

import java.util.Map;

/**
 * StandardEdge
 * <p>
 * package org.hydrofoil.core.standard
 *
 * @author xie_yh
 * @date 2018/7/2 15:58
 */
public class StandardEdge extends StandardElement {

    /**
     * source vertex id
     */
    private GraphVertexId sourceId;

    /**
     * target vertex id
     */
    private GraphVertexId targetId;

    public StandardEdge(GraphElementId elementId,
                        GraphVertexId sourceId,
                        GraphVertexId targetId,
                        Map<String, StandardProperty> propertyMap) {
        super(elementId, propertyMap);
        this.sourceId = sourceId;
        this.targetId = targetId;
    }

    public GraphVertexId sourceId(){
        return sourceId;
    }

    public GraphVertexId targetId(){
        return targetId;
    }

}
