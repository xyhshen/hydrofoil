package org.hydrofoil.core.standard.query;

import org.hydrofoil.common.graph.EdgeDirection;
import org.hydrofoil.common.graph.GraphElementId;

/**
 * EdgeGraphCondition
 * <p>
 * package org.hydrofoil.core.standard.query
 *
 * @author xie_yh
 * @date 2018/7/2 17:42
 */
public final class EdgeGraphCondition extends GraphCondition<EdgeGraphCondition> {

    /**
     * Centric vertex id
     */
    private GraphElementId vertexId;

    /**
     * edge direction
     */
    private EdgeDirection direction;

    public GraphElementId vertexId(){
        return vertexId;
    }

    public EdgeGraphCondition vertexId(GraphElementId vertexId){
        this.vertexId = vertexId;
        return this;
    }

    /**
     * get direction
     * @return direction
     */
    public EdgeDirection direction(){
        return direction;
    }

    public EdgeGraphCondition direction(EdgeDirection direction){
        this.direction = direction;
        return this;
    }
}
