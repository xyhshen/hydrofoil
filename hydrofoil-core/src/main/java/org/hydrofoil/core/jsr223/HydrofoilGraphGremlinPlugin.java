package org.hydrofoil.core.jsr223;

import org.apache.tinkerpop.gremlin.jsr223.AbstractGremlinPlugin;
import org.apache.tinkerpop.gremlin.jsr223.DefaultImportCustomizer;
import org.apache.tinkerpop.gremlin.jsr223.ImportCustomizer;

/**
 * HydrofoilGraphGremlinPlugin
 * <p>
 * package org.hydrofoil.core.jsr223
 *
 * @author xie_yh
 * @date 2018/6/28 17:19
 */
public final class HydrofoilGraphGremlinPlugin extends AbstractGremlinPlugin {

    private final static String NAME = "org.hydrofoil";

    private static ImportCustomizer imports = DefaultImportCustomizer.build().create();

    public HydrofoilGraphGremlinPlugin() {
        super(NAME, imports);
    }
}
