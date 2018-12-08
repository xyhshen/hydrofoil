package org.hydrofoil.core.tinkerpop.structure;

import org.apache.tinkerpop.gremlin.structure.*;
import org.hydrofoil.core.engine.EngineVertex;
import org.hydrofoil.core.tinkerpop.glue.TinkerpopGraphTransit;

import java.util.Collections;
import java.util.Iterator;

/**
 * HydrofoilVertex
 * <p>
 * package org.hydrofoil.core.tinkerpop
 *
 * @author xie_yh
 * @date 2018/7/24 11:34
 */
@SuppressWarnings("unchecked")
public class HydrofoilVertex extends HydrofoilElement implements Vertex {

    public HydrofoilVertex(HydrofoilTinkerpopGraph graph,
                           EngineVertex standardVertex) {
        super(graph, standardVertex);
    }

    @Override
    public Edge addEdge(String label, Vertex inVertex, Object... keyValues) {
        return (Edge) Vertex.Exceptions.edgeAdditionsNotSupported();
    }

    @Override
    public Iterator<Edge> edges(Direction direction, String... edgeLabels) {
        return TinkerpopGraphTransit.listEdgesByVertices(graph, Collections.singletonList(this),direction,edgeLabels);
    }

    @Override
    public Iterator<Vertex> vertices(Direction direction, String... edgeLabels) {
        return TinkerpopGraphTransit.listEdgeVerticesByVertices(graph,Collections.singletonList(this),direction,edgeLabels);
    }

    @Override
    public <V> VertexProperty<V> property(VertexProperty.Cardinality cardinality, String key, V value, Object... keyValues) {
        return (VertexProperty<V>) Element.Exceptions.propertyAdditionNotSupported();
    }

    @Override
    public void remove() {
        throw Vertex.Exceptions.vertexRemovalNotSupported();
    }

    @Override
    public <V> Iterator<VertexProperty<V>> properties(String... propertyKeys) {
        return TinkerpopGraphTransit.listVertexProperties(this,propertyKeys);
    }
}
