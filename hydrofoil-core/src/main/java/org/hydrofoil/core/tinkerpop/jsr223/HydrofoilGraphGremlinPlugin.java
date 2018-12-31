package org.hydrofoil.core.tinkerpop.jsr223;

import org.apache.tinkerpop.gremlin.jsr223.AbstractGremlinPlugin;
import org.apache.tinkerpop.gremlin.jsr223.DefaultImportCustomizer;
import org.apache.tinkerpop.gremlin.jsr223.ImportCustomizer;
import org.hydrofoil.common.configuration.HydrofoilConfiguration;
import org.hydrofoil.core.HydrofoilGraph;
import org.hydrofoil.core.tinkerpop.structure.*;

/**
 * HydrofoilGraphGremlinPlugin
 * <p>
 * package org.hydrofoil.core.jsr223
 *
 * @author xie_yh
 * @date 2018/6/28 17:19
 */
public final class HydrofoilGraphGremlinPlugin extends AbstractGremlinPlugin {

    private final static String INSTANCE_NAME = "org.hydrofoil";

    private static ImportCustomizer imports = DefaultImportCustomizer.build()
            .addClassImports(HydrofoilTinkerpopGraph.class,
                             HydrofoilVertex.class,
                             HydrofoilEdge.class,
                             HydrofoilVertexProperty.class,
                             HydrofoilProperty.class,
                             HydrofoilConfiguration.class,
                             HydrofoilElement.class,
                             HydrofoilGraph.class).create();

    private static final HydrofoilGraphGremlinPlugin INSTANCE = new HydrofoilGraphGremlinPlugin();

    public HydrofoilGraphGremlinPlugin() {
        super(INSTANCE_NAME, imports);
    }

    public static HydrofoilGraphGremlinPlugin instance() {
        return INSTANCE;
    }
}
