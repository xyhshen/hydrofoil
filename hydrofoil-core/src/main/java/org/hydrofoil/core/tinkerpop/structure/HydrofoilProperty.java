package org.hydrofoil.core.tinkerpop.structure;

import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Property;

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

    @Override
    public String key() {
        return null;
    }

    @Override
    public V value() throws NoSuchElementException {
        return null;
    }

    @Override
    public boolean isPresent() {
        return false;
    }

    @Override
    public Element element() {
        return null;
    }

    @Override
    public void remove() {
        throw Exceptions.propertyRemovalNotSupported();
    }
}
