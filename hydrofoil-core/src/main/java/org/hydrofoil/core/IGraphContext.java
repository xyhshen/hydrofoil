package org.hydrofoil.core;

import org.hydrofoil.common.graph.GraphEdgeId;
import org.hydrofoil.common.graph.GraphVertexId;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.core.standard.StandardEdge;
import org.hydrofoil.core.standard.StandardVertex;
import org.hydrofoil.core.standard.query.EdgeGraphCondition;
import org.hydrofoil.core.standard.query.VertexGraphCondition;

import java.io.Closeable;
import java.util.Iterator;

/**
 * GraphContext
 * <p>
 * package org.hydrofoil.core
 *
 * @author xie_yh
 * @date 2018/7/2 16:26
 */
public interface IGraphContext extends Closeable,AutoCloseable {

    /**
     * get vertex by id
     * @param vertexId vertexId
     * @return vertex
     */
    default StandardVertex getVertex(GraphVertexId vertexId){
        return DataUtils.iteratorFirst(listVertices(new VertexGraphCondition().setId(vertexId)));
    }

    /**
     * get edge by id
     * @param edgeId edge id
     * @return edge
     */
    default StandardEdge getEdge(GraphEdgeId edgeId){
        return DataUtils.iteratorFirst(listEdges(new EdgeGraphCondition().setId(edgeId)));
    }

    /**
     *  list vertex
     * @param condition
     * @return
     */
    Iterator<StandardVertex> listVertices(VertexGraphCondition condition);

    /**
     * list edge
     * @param condition
     * @return
     */
    Iterator<StandardEdge> listEdges(EdgeGraphCondition condition);

}
