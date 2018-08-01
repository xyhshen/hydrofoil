package org.hydrofoil.common.graph;

import java.util.Map;

/**
 * GraphEdgeId
 * <p>
 * package org.hydrofoil.common.graph
 *
 * @author xie_yh
 * @date 2018/7/2 16:58
 */
public final class GraphEdgeId extends GraphElementId {

    GraphEdgeId(final String label,final Map<String,Object> unique) {
        super(label,unique);
    }

    @Override
    public boolean equals(final Object otherObj) {
        return otherObj instanceof GraphEdgeId && super.equals(otherObj);
    }

    @Override
    public GraphEdgeId clone(){
        GraphEdgeId graphEdgeId = null;
        try {
            graphEdgeId = (GraphEdgeId) super.clone();
            graphEdgeId.label = label;
            graphEdgeId.unique.putAll(super.unique);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return graphEdgeId;
    }
}
