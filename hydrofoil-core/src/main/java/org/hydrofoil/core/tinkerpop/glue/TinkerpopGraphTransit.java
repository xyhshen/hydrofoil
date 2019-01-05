package org.hydrofoil.core.tinkerpop.glue;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.collections4.MultiMapUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.lang.ArrayUtils;
import org.apache.tinkerpop.gremlin.structure.*;
import org.hydrofoil.common.graph.GraphElementType;
import org.hydrofoil.common.graph.GraphProperty;
import org.hydrofoil.common.graph.GraphVertexId;
import org.hydrofoil.common.graph.QMatch;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.common.util.ArgumentUtils;
import org.hydrofoil.core.engine.*;
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

    private final HydrofoilTinkerpopGraph graph;

    private TinkerpopGraphTransit(final HydrofoilTinkerpopGraph graph){
        this.graph = graph;
    }

    public static TinkerpopGraphTransit of(final HydrofoilTinkerpopGraph graph){
        return new TinkerpopGraphTransit(graph);
    }

    private Iterator<Vertex> returnVertexIterator(Iterator<EngineVertex> vertexIterator){
        return new Iterator<Vertex>() {
            @Override
            public boolean hasNext() {
                return vertexIterator.hasNext();
            }

            @Override
            public Vertex next() {
                final EngineVertex next = vertexIterator.next();
                ArgumentUtils.notNullMessage(next,"vertex is null");
                return new HydrofoilVertex(graph,next);
            }
        };
    }

    /**
     * tinkerpop graph list all vertex
     * @param vertexIds id's
     * @return vertex
     */
    public Iterator<Vertex> listVerticesByIds(Object... vertexIds){
        long length = graph.getConnector().getConfiguration().getLong(TinkerpopDefaultReturnLength);
        Iterator<EngineVertex> vertexIterator = graph.getConnector().vertices(graph.getIdManage().vertexIds(vertexIds)).
                length(length).take();
        return returnVertexIterator(vertexIterator);
    }

    public Iterator<Vertex> executeQuery(VertexGraphQueryRunner runner){
        Iterator<EngineVertex> vertexIterator = runner.take();
        return returnVertexIterator(vertexIterator);
    }

    public Iterator<Edge> executeQuery(EdgeGraphQueryRunner runner){
        Iterator<EngineEdge> edgeIterator = runner.take();
        return new Iterator<Edge>() {
            @Override
            public boolean hasNext() {
                return edgeIterator.hasNext();
            }

            @Override
            public Edge next() {
                final EngineEdge next = edgeIterator.next();
                ArgumentUtils.notNullMessage(next,"edge is null");
                return new HydrofoilEdge(graph,next);
            }
        };
    }

    /**
     *
     * @param condition
     * @return
     */
    public Long countElement(final MultipleCondition condition){
        IGraphQueryRunner graphQueryRunner;
        if(condition.getReturnType() == GraphElementType.vertex){
            graphQueryRunner = graph.getConnector().vertices();
        }else{
            graphQueryRunner = graph.getConnector().edges();
        }
        return graphQueryRunner.
                labels(DataUtils.toArray(condition.getLabels(),String.class)).
                fields(DataUtils.toArray(condition.getFieldQueries(), QMatch.Q.class)).count();
    }

    @SuppressWarnings("unchecked")
    public <E extends Element> Iterator<E> listElements(final MultipleCondition condition){
        IGraphQueryRunner graphQueryRunner;
        if(condition.getReturnType() == GraphElementType.vertex){
            graphQueryRunner = graph.getConnector().vertices();
        }else{
            graphQueryRunner = graph.getConnector().edges();
        }
        graphQueryRunner.
                labels(DataUtils.toArray(condition.getLabels(),String.class)).
                fields(DataUtils.toArray(condition.getFieldQueries(), QMatch.Q.class)).
                offset(condition.getStart()).
                length(condition.getLimit());
        return condition.getReturnType() == GraphElementType.vertex?
                (Iterator<E>) executeQuery((VertexGraphQueryRunner) graphQueryRunner) :
                (Iterator<E>) executeQuery((EdgeGraphQueryRunner) graphQueryRunner);
    }

    /**
     * tinkerpop graph list all edge
     * @param edgeIds id's
     * @return edge
     */
    public Iterator<Edge> listEdgesByIds(Object... edgeIds){
        long length = graph.getConnector().getConfiguration().getLong(TinkerpopDefaultReturnLength);
        return executeQuery(graph.getConnector().edges(graph.getIdManage().edgeIds(edgeIds)).
                length(length));
    }

    private EngineVertex[] toVertexArray(final Collection<HydrofoilVertex> vertices){
        return CollectionUtils.emptyIfNull(vertices).stream().
                map(vertex -> (EngineVertex)vertex.standard()).toArray(EngineVertex[]::new);
    }

    public Iterator<Edge> listEdgesByVertices(
            Collection<HydrofoilVertex> vertices,
            Direction direction,
            String ...labels
    ){
        MultipleCondition condition = new MultipleCondition();
        condition.getLabels().addAll(Arrays.asList(labels));
        return listEdgesByVertices(vertices,direction,condition);
    }

    public Iterator<Edge> listEdgesByVertices(
            final Collection<HydrofoilVertex> vertices,
            final Direction direction,
            final MultipleCondition condition
    ){
        return executeQuery(graph.getConnector().edges().
                vertices(toVertexArray(vertices)).
                labels(DataUtils.toArray(condition.getLabels(),String.class)).
                fields(DataUtils.toArray(condition.getFieldQueries(), QMatch.Q.class)).
                offset(condition.getStart()).
                length(condition.getLimit()).
                direction(TinkerpopElementUtils.toStdDirection(direction)));
    }

    public MultiValuedMap<GraphVertexId,Edge> listEdgesByVerticesFlatMap(
            final Collection<HydrofoilVertex> vertices,
            final Direction direction,
            final MultipleCondition condition
    ){
        MultiValuedMap<GraphVertexId,Edge> vertex2Edge = DataUtils.newMultiMapWithMaxSize(vertices.size() * 2);
        final Iterator<Edge> edges = listEdgesByVertices(vertices, direction, condition);
        IteratorUtils.forEach(edges,(e)->{
            HydrofoilEdge edge = (HydrofoilEdge) e;
            final EngineEdge engineEdge = (EngineEdge) edge.standard();
            if(direction == Direction.IN || direction == Direction.BOTH){
                vertex2Edge.put(engineEdge.targetId(),edge);
            }
            if(direction == Direction.OUT|| direction == Direction.BOTH){
                vertex2Edge.put(engineEdge.sourceId(),edge);
            }
        });
        return vertex2Edge;
    }

    @SuppressWarnings("unchecked")
    public Iterator<Vertex> listEdgeVerticesByVertices(
            final Collection<HydrofoilVertex> vertices,
            final Direction direction,
            final String ...labels
    ){
        MultipleCondition condition = new MultipleCondition();
        condition.getLabels().addAll(Arrays.asList(labels));
        return (Iterator<Vertex>) listEdgeVerticesByVertices(vertices,direction,condition,false);
    }

    public Object listEdgeVerticesByVertices(
            final Collection<HydrofoilVertex> vertices,
            final Direction direction,
            final MultipleCondition condition,
            final boolean flatMap
    ){
        final EngineVertex[] engineVertices = toVertexArray(vertices);
        MultiValuedMap<GraphVertexId,GraphVertexId> vertex2Vertex = MultiMapUtils.newSetValuedHashMap();
        Iterator<Edge> edgeIterator = executeQuery(graph.getConnector().edges().
                vertices(engineVertices).
                labels(DataUtils.toArray(condition.getLabels(),String.class)).
                direction(TinkerpopElementUtils.toStdDirection(direction)));
        edgeIterator.forEachRemaining((e)->{
            HydrofoilEdge edge = (HydrofoilEdge) e;
            EngineEdge engineEdge = (EngineEdge) edge.standard();
            if(direction == Direction.IN || direction == Direction.BOTH){
                vertex2Vertex.put(engineEdge.sourceId(),engineEdge.targetId());
            }
            if(direction == Direction.OUT|| direction == Direction.BOTH){
                vertex2Vertex.put(engineEdge.targetId(),engineEdge.sourceId());
            }
        });
        Set<GraphVertexId> vertexIds = new HashSet<>(vertex2Vertex.keySet());
        for(EngineVertex vertex:engineVertices){
            vertexIds.remove(vertex.elementId());
        }
        if(vertexIds.isEmpty()){
            return flatMap?MultiMapUtils.emptyMultiValuedMap():IteratorUtils.emptyIterator();
        }

        final List<GraphVertexId> l = DataUtils.range(new ArrayList<>(vertexIds), condition.getStart(), condition.getLimit());
        final Iterator<Vertex> vertexIterator = listVerticesByIds(DataUtils.toArray(l, GraphVertexId.class));
        if(!flatMap){
            return vertexIterator;
        }

        MultiValuedMap<GraphVertexId,Vertex> vertexId2Vertex = MultiMapUtils.newSetValuedHashMap();
        vertexIterator.forEachRemaining(v -> {
            HydrofoilVertex vertex = (HydrofoilVertex) v;
            final Collection<GraphVertexId> vl = vertex2Vertex.get((GraphVertexId) vertex.standard().elementId());
            for(GraphVertexId vid:vl){
                vertexId2Vertex.put(vid,v);
            }
        });
        return vertexId2Vertex;
    }

    public <V> Iterator<VertexProperty<V>> listVertexProperties(HydrofoilVertex vertex,String ...propertyLabels){
        EngineElement standard = vertex.standard();
        Collection<EngineProperty> standardProperties = standard.properties(propertyLabels);
        if(CollectionUtils.isEmpty(standardProperties)){
            return Collections.emptyIterator();
        }
        return standardProperties.stream().
                map(p->(VertexProperty<V>)new HydrofoilVertexProperty<V>(vertex,p)).iterator();
    }

    @SuppressWarnings("unchecked")
    public <U> Iterator<Property<U>> listProperties(final HydrofoilVertexProperty vertexProperty, String... propertyKeys){
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

    public boolean checkConditionOperator(final MultipleCondition condition, final IGraphQueryRunner.OperationType type){
        IGraphQueryRunner graphQueryRunner;
        if(condition.getReturnType() == GraphElementType.vertex){
            graphQueryRunner = graph.getConnector().vertices();
        }else{
            graphQueryRunner = graph.getConnector().edges();
        }
        return graphQueryRunner.
                labels(DataUtils.toArray(condition.getLabels(),String.class)).
                fields(DataUtils.toArray(condition.getFieldQueries(), QMatch.Q.class)).
                offset(condition.getStart()).
                length(condition.getLimit()).
                operable(type);
    }

}
