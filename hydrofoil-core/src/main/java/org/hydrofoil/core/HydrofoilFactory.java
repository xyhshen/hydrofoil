package org.hydrofoil.core;

import org.hydrofoil.common.configuration.HydrofoilConfiguration;
import org.hydrofoil.core.tinkerpop.structure.HydrofoilTinkerpopGraph;

/**
 * HydrofoilFactory
 * <p>
 * package org.hydrofoil.core
 *
 * @author xie_yh
 * @date 2018/7/2 15:17
 */
public final class HydrofoilFactory {

    /**
     * create graph
     * @param configuration graph config
     * @return connector
     * @throws Exception
     */
    public static HydrofoilGraph create(HydrofoilConfiguration configuration) throws Exception {
        HydrofoilGraph graph = new HydrofoilGraph();
        graph.init(configuration);
        return graph;
    }

    /**
     * create graph,to tinkerpop style
     * @param configuration graph config
     * @return tinkerpop graph object
     * @throws Exception
     */
    public static HydrofoilTinkerpopGraph open(HydrofoilConfiguration configuration)  throws Exception {
        HydrofoilGraph graph = create(configuration);
        return new HydrofoilTinkerpopGraph(graph);
    }

}
