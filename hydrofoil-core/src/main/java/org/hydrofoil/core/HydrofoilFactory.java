package org.hydrofoil.core;

import org.hydrofoil.common.configuration.HydrofoilConfiguration;
import org.hydrofoil.core.tinkerpop.HydrofoilGraph;

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
     * connect graph
     * @param configuration graph config
     * @return connector
     * @throws Exception
     */
    public static HydrofoilConnector connect(HydrofoilConfiguration configuration) throws Exception {
        HydrofoilConnector connector = new HydrofoilConnector();
        connector.init(configuration);
        return connector;
    }

    /**
     * connect graph,to tinkerpop style
     * @param configuration graph config
     * @return tinkerpop graph object
     * @throws Exception
     */
    public static HydrofoilGraph openTinkerpop(HydrofoilConfiguration configuration)  throws Exception {
        HydrofoilConnector connector = connect(configuration);
        return new HydrofoilGraph(connector);
    }

}
