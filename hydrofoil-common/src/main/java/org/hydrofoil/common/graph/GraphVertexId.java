package org.hydrofoil.common.graph;

import org.apache.commons.lang3.ObjectUtils;
import org.hydrofoil.common.util.bean.KeyValueEntity;

/**
 * GraphVertexId
 * <p>
 * package org.hydrofoil.common.graph
 *
 * @author xie_yh
 * @date 2018/7/2 15:49
 */
public final class GraphVertexId extends GraphElementId{
    GraphVertexId(final String label,final KeyValueEntity unique) {
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
            graphVertexId.unique = ObjectUtils.clone(super.unique);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return graphVertexId;
    }
}
