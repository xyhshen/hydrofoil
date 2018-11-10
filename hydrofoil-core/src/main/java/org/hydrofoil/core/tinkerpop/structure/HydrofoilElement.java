package org.hydrofoil.core.tinkerpop.structure;

import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.hydrofoil.core.engine.EngineElement;

/**
 * HydrofoilElement
 * <p>
 * package org.hydrofoil.core.tinkerpop
 *
 * @author xie_yh
 * @date 2018/7/24 11:30
 */
public abstract class HydrofoilElement implements Element {

    protected final EngineElement engineElement;

    protected final HydrofoilTinkerpopGraph graph;

    protected final Object id;

    HydrofoilElement(HydrofoilTinkerpopGraph graph,
                               EngineElement engineElement){
        this.graph = graph;
        this.engineElement = engineElement;
        this.id = graph.getIdManage().tinkerpopId(engineElement.elementId());
    }

    @Override
    public Object id() {
        return id;
    }

    @Override
    public String label() {
        if(engineElement == null){
            return null;
        }
        return engineElement.elementId().label();
    }

    @Override
    public Graph graph() {
        return graph;
    }

    /**
     * get engine element
     * @return element
     */
    public EngineElement standard(){
        return engineElement;
    }
}
