package org.hydrofoil.core.engine;

import org.hydrofoil.common.graph.GraphElementId;
import org.hydrofoil.common.graph.GraphVertexId;
import org.hydrofoil.core.engine.internal.EnginePropertySet;

/**
 * EngineEdge
 * <p>
 * package org.hydrofoil.core.engine
 *
 * @author xie_yh
 * @date 2018/7/2 15:58
 */
public class EngineEdge extends EngineElement {

    /**
     * source vertex id
     */
    private GraphVertexId sourceId;

    /**
     * target vertex id
     */
    private GraphVertexId targetId;

    public EngineEdge(GraphElementId elementId,
                      GraphVertexId sourceId,
                      GraphVertexId targetId,
                      EnginePropertySet propertySet) {
        super(elementId, propertySet);
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
