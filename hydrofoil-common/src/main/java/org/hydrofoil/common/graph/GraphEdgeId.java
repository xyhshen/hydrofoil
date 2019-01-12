package org.hydrofoil.common.graph;

import org.apache.commons.lang3.ObjectUtils;
import org.hydrofoil.common.util.bean.KeyValueEntity;

/**
 * GraphEdgeId
 * <p>
 * package org.hydrofoil.common.graph
 *
 * @author xie_yh
 * @date 2018/7/2 16:58
 */
public final class GraphEdgeId extends GraphElementId {

    GraphEdgeId(final String label,final KeyValueEntity unique) {
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
            graphEdgeId.unique = ObjectUtils.clone(super.unique);
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return graphEdgeId;
    }
}
