package org.hydrofoil.common.graph;

/**
 * GraphEdgeId
 * <p>
 * package org.hydrofoil.common.graph
 *
 * @author xie_yh
 * @date 2018/7/2 16:58
 */
public final class GraphEdgeId extends GraphElementId<GraphEdgeId> {
    public GraphEdgeId(String label) {
        super(label);
    }

    @Override
    public boolean equals(Object otherObj){
        if(otherObj instanceof GraphEdgeId){
           return super.equals(otherObj);
        }
        return false;
    }

    @Override
    public GraphEdgeId clone(){
        GraphEdgeId graphEdgeId = new GraphEdgeId(label());
        graphEdgeId.unique.putAll(super.unique);
        return graphEdgeId;
    }
}
