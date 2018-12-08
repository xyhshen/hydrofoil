package org.hydrofoil.core.tinkerpop.glue;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang.ArrayUtils;
import org.apache.tinkerpop.gremlin.structure.*;
import org.hydrofoil.common.graph.GraphProperty;
import org.hydrofoil.common.graph.GraphVertexId;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.core.engine.EngineEdge;
import org.hydrofoil.core.engine.EngineElement;
import org.hydrofoil.core.engine.EngineProperty;
import org.hydrofoil.core.engine.EngineVertex;
import org.hydrofoil.core.engine.query.EdgeGraphQueryRunner;
import org.hydrofoil.core.engine.query.VertexGraphQueryRunner;
import org.hydrofoil.core.tinkerpop.structure.*;

import java.util.*;

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

    private static Iterator<Vertex> returnVertexIterator(HydrofoilTinkerpopGraph graph, Iterator<EngineVertex> vertexIterator){
        return new Iterator<Vertex>() {
            @Override
            public boolean hasNext() {
                return vertexIterator.hasNext();
            }

            @Override
            public Vertex next() {
                final EngineVertex next = vertexIterator.next();
                ParameterUtils.nullMessage(next,"vertex is null");
                return new HydrofoilVertex(graph,next);
            }
        };
    }

    /**
     * tinkerpop graph list all vertex
     * @param graph graph instance
     * @param vertexIds id's
     * @return vertex
     */
    public static Iterator<Vertex> listVerticesByIds(HydrofoilTinkerpopGraph graph, Object... vertexIds){
        long length = graph.getConnector().getConfiguration().getLong(TinkerpopDefaultReturnLength);
        Iterator<EngineVertex> vertexIterator = graph.getConnector().vertices(graph.getIdManage().vertexIds(vertexIds)).
                length(length).take();
        return returnVertexIterator(graph,vertexIterator);
    }

    public static Iterator<Vertex> executeQuery(HydrofoilTinkerpopGraph graph, VertexGraphQueryRunner runner){
        Iterator<EngineVertex> vertexIterator = runner.take();
        return returnVertexIterator(graph,vertexIterator);
    }

    public static Iterator<Edge> executeQuery(HydrofoilTinkerpopGraph graph, EdgeGraphQueryRunner runner){
        Iterator<EngineEdge> edgeIterator = runner.take();
        return new Iterator<Edge>() {
            @Override
            public boolean hasNext() {
                return edgeIterator.hasNext();
            }

            @Override
            public Edge next() {
                final EngineEdge next = edgeIterator.next();
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
    public static Iterator<Edge> listEdgesByIds(HydrofoilTinkerpopGraph graph, Object... edgeIds){
        long length = graph.getConnector().getConfiguration().getLong(TinkerpopDefaultReturnLength);
        return executeQuery(graph,graph.getConnector().edges(graph.getIdManage().edgeIds(edgeIds)).
                length(length));
    }

    private static EngineVertex[] toVertexArray(final Collection<HydrofoilVertex> vertices){
        return CollectionUtils.emptyIfNull(vertices).stream().
                map(vertex -> (EngineVertex)vertex.standard()).toArray(EngineVertex[]::new);
    }

    public static Iterator<Edge> listEdgesByVertices(HydrofoilTinkerpopGraph graph, Collection<HydrofoilVertex> vertices,
                                                     Direction direction, String ...labels){
        return executeQuery(graph,graph.getConnector().edges().
                vertices(toVertexArray(vertices)).
                labels(labels).direction(TinkerpopElementUtils.toStdDirection(direction)));
    }

    public static Iterator<Vertex> listEdgeVerticesByVertices(HydrofoilTinkerpopGraph graph, Collection<HydrofoilVertex> vertices,
                                                              Direction direction, String ...labels){
        Set<GraphVertexId> vertexIds = new HashSet<>();
        final EngineVertex[] engineVertices = toVertexArray(vertices);
        Iterator<Edge> edgeIterator = executeQuery(graph, graph.getConnector().edges().
                vertices(engineVertices).
                labels(labels).direction(TinkerpopElementUtils.toStdDirection(direction)));
        edgeIterator.forEachRemaining((e)->{
            HydrofoilEdge edge = (HydrofoilEdge) e;
            EngineEdge engineEdge = (EngineEdge) edge.standard();
            if(direction == Direction.IN || direction == Direction.BOTH){
                vertexIds.add(engineEdge.sourceId());
            }
            if(direction == Direction.OUT|| direction == Direction.BOTH){
                vertexIds.add(engineEdge.targetId());
            }
        });
        for(EngineVertex vertex:engineVertices){
            vertexIds.remove(vertex.elementId());
        }
        if(vertexIds.isEmpty()){
            return IteratorUtils.emptyIterator();
        }

        return listVerticesByIds(graph,DataUtils.toArray(vertexIds,GraphVertexId.class));
    }

    public static <V> Iterator<VertexProperty<V>> listVertexProperties(HydrofoilVertex vertex,String ...propertyLabels){
        EngineElement standard = vertex.standard();
        Collection<EngineProperty> standardProperties = standard.properties(propertyLabels);
        if(CollectionUtils.isEmpty(standardProperties)){
            return Collections.emptyIterator();
        }
        return standardProperties.stream().
                map(p->(VertexProperty<V>)new HydrofoilVertexProperty<V>(vertex,p)).iterator();
    }

    @SuppressWarnings("unchecked")
    public static <U> Iterator<Property<U>> listProperties(final HydrofoilVertexProperty vertexProperty, String... propertyKeys){
        List<Property<U>> properties = new ArrayList<>(1);
        if(vertexProperty.standard().isComplex()){
            final Map<String, GraphProperty> propertyMap = vertexProperty.standard().properties();
            Set<String> nameKeySet;
            if(ArrayUtils.isEmpty(propertyKeys)){
                nameKeySet = vertexProperty.standard().properties().keySet();
            }else{
                nameKeySet = DataUtils.newSetWithMaxSize(propertyKeys.length);
                nameKeySet.addAll(Arrays.asList(propertyKeys));
            }
            nameKeySet.forEach((key)->{
                final GraphProperty graphProperty = vertexProperty.standard().properties().get(key);
                if(graphProperty == null){
                    return;
                }
                properties.add(new HydrofoilProperty(vertexProperty,graphProperty,key));
            });
        }else{
            properties.add(new HydrofoilProperty(vertexProperty,vertexProperty.standard().property(),
                    vertexProperty.standard().label()));
        }
        return properties.iterator();
    }

}
