package org.hydrofoil.core.tinkerpop.structure;

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

    protected final StandardElement standardElement;

    protected final HydrofoilGraph graph;

    protected final Object id;

    HydrofoilElement(HydrofoilGraph graph,
                               StandardElement standardElement){
        this.graph = graph;
        this.standardElement = standardElement;
        this.id = graph.getIdManage().tinkerpopId(standardElement.elementId());
    }

    @Override
    public Object id() {
        return id;
    }

    @Override
    public String label() {
        if(standardElement == null){
            return null;
        }
        return standardElement.elementId().label();
    }

    @Override
    public Graph graph() {
        return graph;
    }

    /**
     * get standard element
     * @return element
     */
    public StandardElement standard(){
        return standardElement;
    }
}
