package org.hydrofoil.core.tinkerpop.structure;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.core.standard.StandardEdge;
import org.hydrofoil.core.tinkerpop.glue.TinkerpopGraphTransit;

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

    public HydrofoilEdge(HydrofoilGraph graph,
                         StandardEdge standardEdge) {
        super(graph, standardEdge);
    }

    @Override
    public Iterator<Vertex> vertices(Direction direction) {
        if(direction == Direction.IN){
            return IteratorUtils.arrayIterator(inVertex());
        }
        if(direction == Direction.OUT){
            return IteratorUtils.arrayIterator(outVertex());
        }
        return bothVertices();
    }

    @Override
    public Vertex outVertex() {
        StandardEdge standardEdge = (StandardEdge) standardElement;
        return DataUtils.iteratorFirst(TinkerpopGraphTransit.listVerticesByIds(graph,
                standardEdge.sourceId()));
    }

    @Override
    public Vertex inVertex() {
        StandardEdge standardEdge = (StandardEdge) standardElement;
        return DataUtils.iteratorFirst(TinkerpopGraphTransit.listVerticesByIds(graph,
                standardEdge.targetId()));
    }

    @Override
    public Iterator<Vertex> bothVertices() {
        return IteratorUtils.arrayIterator(outVertex(),inVertex());
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
