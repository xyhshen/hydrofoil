package org.hydrofoil.core.jsr223;

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

    private static final ImportCustomizer imports = DefaultImportCustomizer.build().create();

    public HydrofoilGraphGremlinPlugin() {
        super(NAME, imports);
    }
}
