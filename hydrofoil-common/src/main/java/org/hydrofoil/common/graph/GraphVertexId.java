package org.hydrofoil.common.graph;

import java.util.Map;

/**
 * GraphVertexId
 * <p>
 * package org.hydrofoil.common.graph
 *
 * @author xie_yh
 * @date 2018/7/2 15:49
 */
public final class GraphVertexId extends GraphElementId{
    GraphVertexId(final String label,final Map<String,Object> unique) {
        super(label,unique);
    }

    @Override
    public int hashCode(){
        return super.hashCode();
    }

    @Override
    public boolean equals(final Object otherObj) {
        return otherObj instanceof GraphVertexId && super.equals(otherObj);
    }

    @Override
    public GraphVertexId clone(){
        GraphVertexId graphVertexId = null;
        try {
            graphVertexId = (GraphVertexId) super.clone();
            graphVertexId.label = label;
            graphVertexId.unique.putAll(super.unique);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return graphVertexId;
    }
}
