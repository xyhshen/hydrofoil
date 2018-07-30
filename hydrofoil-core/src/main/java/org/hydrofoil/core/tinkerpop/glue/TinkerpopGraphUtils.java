package org.hydrofoil.core.tinkerpop.glue;

import org.apache.commons.collections4.map.LinkedMap;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.core.standard.StandardEdge;
import org.hydrofoil.core.standard.StandardVertex;
import org.hydrofoil.core.tinkerpop.structure.HydrofoilEdge;
import org.hydrofoil.core.tinkerpop.structure.HydrofoilGraph;
import org.hydrofoil.core.tinkerpop.structure.HydrofoilVertex;

import java.util.Iterator;

/**
 * TinkerpopGraphUtils
 * <p>
 * package org.hydrofoil.core.tinkerpop.glue
 *
 * @author xie_yh
 * @date 2018/7/27 18:13
 */
public final class TinkerpopGraphUtils {

    private static Long EMPTY_MAX_LENGTH = 20000L;

    /**
     * tinkerpop graph list all vertex
     * @param graph graph instance
     * @param vertexIds id's
     * @return vertex
     */
    public static Iterator<Vertex> listVertexByIds(HydrofoilGraph graph, Object... vertexIds){
        Iterator<StandardVertex> vertexIterator = graph.getConnector().vertices(graph.getIdManage().vertexIds(vertexIds)).
                length(EMPTY_MAX_LENGTH).take();
        return new Iterator<Vertex>() {
            @Override
            public boolean hasNext() {
                return vertexIterator.hasNext();
            }

            @Override
            public Vertex next() {
                final StandardVertex next = vertexIterator.next();
                ParameterUtils.nullMessage(next,"vertex is null");
                return new HydrofoilVertex(graph,next);
            }
        };
    }

    /**
     * tinkerpop graph list all edge
     * @param graph graph instance
     * @param edgeIds id's
     * @return edge
     */
    public static Iterator<Edge> listEdgeByIds(HydrofoilGraph graph, Object... edgeIds){
        Iterator<StandardEdge> edgeIterator = graph.getConnector().edges(graph.getIdManage().edgeIds(edgeIds)).
                length(EMPTY_MAX_LENGTH).take();
        return new Iterator<Edge>() {
            @Override
            public boolean hasNext() {
                return edgeIterator.hasNext();
            }

            @Override
            public Edge next() {
                final StandardEdge next = edgeIterator.next();
                ParameterUtils.nullMessage(next,"edge is null");
                return new HydrofoilEdge(graph,next);
            }
        };
    }

    public static void main(String[] args){
        LinkedMap<String,Object> map = new LinkedMap<>();
        map.get("");
    }

}
