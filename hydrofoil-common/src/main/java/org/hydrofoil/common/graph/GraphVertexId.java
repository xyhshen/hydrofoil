package org.hydrofoil.common.graph;

/**
 * GraphVertexId
 * <p>
 * package org.hydrofoil.common.graph
 *
 * @author xie_yh
 * @date 2018/7/2 15:49
 */
public final class GraphVertexId extends GraphElementId{
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
}
