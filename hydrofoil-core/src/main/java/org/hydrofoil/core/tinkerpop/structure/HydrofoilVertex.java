package org.hydrofoil.core.tinkerpop.structure;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.tinkerpop.gremlin.structure.*;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.core.standard.StandardProperty;
import org.hydrofoil.core.standard.StandardVertex;

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

    public HydrofoilVertex(HydrofoilGraph graph,
                           StandardVertex standardVertex) {
        super(graph, standardVertex);
    }

    @Override
    public Edge addEdge(String label, Vertex inVertex, Object... keyValues) {
        return (Edge) Vertex.Exceptions.edgeAdditionsNotSupported();
    }

    @Override
    public Iterator<Edge> edges(Direction direction, String... edgeLabels) {
        return null;
    }

    @Override
    public Iterator<Vertex> vertices(Direction direction, String... edgeLabels) {
        return null;
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
        ParameterUtils.mustTrueMessage(ArrayUtils.isNotEmpty(propertyKeys),"property keys not is empty");
        for(String propertyKey:propertyKeys){
            StandardProperty property = standardElement.property(propertyKey);
        }
        return null;
    }
}
