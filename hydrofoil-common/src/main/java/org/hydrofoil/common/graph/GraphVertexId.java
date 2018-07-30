package org.hydrofoil.common.graph;

/**
 * GraphVertexId
 * <p>
 * package org.hydrofoil.common.graph
 *
 * @author xie_yh
 * @date 2018/7/2 15:49
 */
public final class GraphVertexId extends GraphElementId<GraphVertexId>{
    public GraphVertexId(String label) {
        super(label);
    }

    @Override
    public boolean equals(Object otherObj){
        if(otherObj instanceof GraphVertexId){
            return super.equals(otherObj);
        }
        return false;
    }

    @Override
    public GraphVertexId clone(){
        GraphVertexId graphVertexId = new GraphVertexId(label());
        graphVertexId.unique.putAll(super.unique);
        return graphVertexId;
    }
}
