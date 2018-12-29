package org.hydrofoil.core.tinkerpop.structure;

import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.hydrofoil.core.engine.EngineProperty;
import org.hydrofoil.core.tinkerpop.glue.TinkerpopGraphTransit;

import java.util.Iterator;
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

    private final EngineProperty engineProperty;

    private final HydrofoilVertex vertex;

    public HydrofoilVertexProperty(final HydrofoilVertex vertex,
                                   final EngineProperty engineProperty){
        this.vertex = vertex;
        this.engineProperty = engineProperty;
    }

    public EngineProperty standard(){
        return engineProperty;
    }

    @Override
    public String key() {
        return engineProperty.label();
    }

    @SuppressWarnings("unchecked")
    @Override
    public V value() throws NoSuchElementException {
        if(engineProperty.isComplex()){
            return (V) engineProperty.asValueMap();
        }
        return (V) engineProperty.property().content();
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
        return engineProperty.id();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <V> Property<V> property(String key, V value) {
        return (Property<V>) Vertex.Exceptions.edgeAdditionsNotSupported();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <U> Iterator<Property<U>> properties(String... propertyKeys) {
        return TinkerpopGraphTransit.of(vertex.graph).listProperties(this,propertyKeys);
    }
}
