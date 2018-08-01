package org.hydrofoil.core.tinkerpop.glue;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.hydrofoil.common.graph.GraphVertexId;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.core.standard.StandardEdge;
import org.hydrofoil.core.standard.StandardVertex;
import org.hydrofoil.core.standard.query.EdgeGraphQueryRunner;
import org.hydrofoil.core.tinkerpop.structure.HydrofoilEdge;
import org.hydrofoil.core.tinkerpop.structure.HydrofoilGraph;
import org.hydrofoil.core.tinkerpop.structure.HydrofoilVertex;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.hydrofoil.common.configuration.HydrofoilConfigurationItem.TinkerpopDefaultReturnLength;

/**
 * TinkerpopGraphTransit
 * <p>
 * package org.hydrofoil.core.tinkerpop.glue
 *
 * @author xie_yh
 * @date 2018/7/27 18:13
 */
public final class TinkerpopGraphTransit {

    /**
     * tinkerpop graph list all vertex
     * @param graph graph instance
     * @param vertexIds id's
     * @return vertex
     */
    public static Iterator<Vertex> listVerticesByIds(HydrofoilGraph graph, Object... vertexIds){
        long length = graph.getConnector().getConfiguration().getLong(TinkerpopDefaultReturnLength);
        Iterator<StandardVertex> vertexIterator = graph.getConnector().vertices(graph.getIdManage().vertexIds(vertexIds)).
                length(length).take();
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

    private static Iterator<Edge> executeQuery(HydrofoilGraph graph,EdgeGraphQueryRunner runner){
        Iterator<StandardEdge> edgeIterator = runner.take();
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

    /**
     * tinkerpop graph list all edge
     * @param graph graph instance
     * @param edgeIds id's
     * @return edge
     */
    public static Iterator<Edge> listEdgesByIds(HydrofoilGraph graph, Object... edgeIds){
        long length = graph.getConnector().getConfiguration().getLong(TinkerpopDefaultReturnLength);
        return executeQuery(graph,graph.getConnector().edges(graph.getIdManage().edgeIds(edgeIds)).
                length(length));
    }

    public static Iterator<Edge> listEdgesByVertex(HydrofoilGraph graph, HydrofoilVertex vertex,
                                                   Direction direction, String ...labels){
        return executeQuery(graph,graph.getConnector().edges().
                vertex((StandardVertex) vertex.standard()).
                labels(labels).direction(TinkerpopElementUtils.toStdDirection(direction)));
    }

    public static Iterator<Vertex> listEdgeVerticesByVertex(HydrofoilGraph graph, HydrofoilVertex vertex,
                                                   Direction direction, String ...labels){
        Set<GraphVertexId> vertexIds = new HashSet<>();
        Iterator<Edge> edgeIterator = executeQuery(graph, graph.getConnector().edges().
                vertex((StandardVertex) vertex.standard()).
                labels(labels).direction(TinkerpopElementUtils.toStdDirection(direction)));
        edgeIterator.forEachRemaining((e)->{
            HydrofoilEdge edge = (HydrofoilEdge) e;
            StandardEdge standardEdge = (StandardEdge) edge.standard();
            vertexIds.add(standardEdge.sourceId());
            vertexIds.add(standardEdge.targetId());
        });
        vertexIds.remove(vertex.standard().elementId());
        if(vertexIds.isEmpty()){
            return IteratorUtils.emptyIterator();
        }
        return listVerticesByIds(graph,vertexIds.toArray(new GraphVertexId[vertexIds.size()]));
    }

}
