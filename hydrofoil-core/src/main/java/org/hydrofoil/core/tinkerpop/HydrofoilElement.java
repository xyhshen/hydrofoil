package org.hydrofoil.core.tinkerpop;

import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.hydrofoil.core.standard.StandardElement;

/**
 * HydrofoilElement
 * <p>
 * package org.hydrofoil.core.tinkerpop
 *
 * @author xie_yh
 * @date 2018/7/24 11:30
 */
public abstract class HydrofoilElement implements Element {

    protected StandardElement standardElement;

    protected HydrofoilGraph graph;

    HydrofoilElement(HydrofoilGraph graph,
                               StandardElement standardElement){
        this.graph = graph;
        this.standardElement = standardElement;
    }

    @Override
    public Object id() {
        return null;
    }

    @Override
    public String label() {
        if(standardElement == null){
            return null;
        }
        return standardElement.elementId().label();
    }

    @Override
    public void remove() {

    }

    @Override
    public Graph graph() {
        return graph;
    }
}
