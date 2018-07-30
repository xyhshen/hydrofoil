package org.hydrofoil.core.tinkerpop.structure;

import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.hydrofoil.core.standard.StandardProperty;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * HydrofoilVertexProperty
 * <p>
 * package org.hydrofoil.core.tinkerpop.structure
 *
 * @author xie_yh
 * @date 2018/7/26 16:52
 */
public final class HydrofoilVertexProperty<V> implements VertexProperty<V>{

    private final StandardProperty standardProperty;

    private final HydrofoilVertex vertex;

    public HydrofoilVertexProperty(final HydrofoilVertex vertex,
                                   final StandardProperty standardProperty){
        this.vertex = vertex;
        this.standardProperty = standardProperty;
    }

    @Override
    public String key() {
        return standardProperty.label();
    }

    @SuppressWarnings("unchecked")
    @Override
    public V value() throws NoSuchElementException {
        return (V) standardProperty.simple().content();
    }

    @Override
    public boolean isPresent() {
        return true;
    }

    @Override
    public Vertex element() {
        return vertex;
    }

    @Override
    public void remove() {
        throw Property.Exceptions.propertyRemovalNotSupported();
    }

    @Override
    public Object id() {
        return standardProperty.id();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> Property<V> property(String key, V value) {
        return (Property<V>) Vertex.Exceptions.edgeAdditionsNotSupported();
    }

    @Override
    public <U> Iterator<Property<U>> properties(String... propertyKeys) {
        List<Property<U>> properties = new LinkedList<>();
        if(!standardProperty.isComplex()){
            //properties.add(new HydrofoilProperty<>());
        }
        return null;
    }
}
