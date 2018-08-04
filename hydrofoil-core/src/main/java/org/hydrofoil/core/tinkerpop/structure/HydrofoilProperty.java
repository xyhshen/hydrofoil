package org.hydrofoil.core.tinkerpop.structure;

import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.hydrofoil.common.graph.GraphProperty;

import java.util.NoSuchElementException;

/**
 * HydrofoilProperty
 * <p>
 * package org.hydrofoil.core.tinkerpop.structure
 *
 * @author xie_yh
 * @date 2018/7/26 16:56
 */
public final class HydrofoilProperty<V> implements Property<V> {

    private final HydrofoilVertexProperty vertexProperty;

    private final GraphProperty graphProperty;

    public HydrofoilProperty(final HydrofoilVertexProperty vertexProperty,
                             final GraphProperty graphProperty){
        this.vertexProperty = vertexProperty;
        this.graphProperty = graphProperty;
    }

    @Override
    public String key() {
        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public V value() throws NoSuchElementException {
        return (V) graphProperty.content();
    }

    @Override
    public boolean isPresent() {
        return graphProperty != null;
    }

    @Override
    public Element element() {
        return vertexProperty;
    }

    @Override
    public void remove() {
        throw Exceptions.propertyRemovalNotSupported();
    }
}
