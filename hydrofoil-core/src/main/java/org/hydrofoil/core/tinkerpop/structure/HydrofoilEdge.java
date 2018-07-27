package org.hydrofoil.core.tinkerpop.structure;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.hydrofoil.core.standard.StandardEdge;

import java.util.Iterator;

/**
 * HydrofoilEdge
 * <p>
 * package org.hydrofoil.core.tinkerpop
 *
 * @author xie_yh
 * @date 2018/7/24 11:41
 */
@SuppressWarnings("unchecked")
public class HydrofoilEdge extends HydrofoilElement implements Edge {

    protected HydrofoilEdge(HydrofoilGraph graph,
                            StandardEdge standardEdge) {
        super(graph, standardEdge);
    }

    @Override
    public Iterator<Vertex> vertices(Direction direction) {
        return null;
    }

    @Override
    public Vertex outVertex() {
        return null;
    }

    @Override
    public Vertex inVertex() {
        return null;
    }

    @Override
    public Iterator<Vertex> bothVertices() {
        return null;
    }

    @Override
    public <V> Property<V> property(String key, V value) {
        return (Property<V>) Edge.Exceptions.propertyAdditionNotSupported();
    }

    @Override
    public void remove() {
        throw Edge.Exceptions.edgeRemovalNotSupported();
    }

    @Override
    public <V> Iterator<Property<V>> properties(String... propertyKeys) {
        return null;
    }
}