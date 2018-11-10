package org.hydrofoil.core.engine;

import org.hydrofoil.common.graph.GraphElementId;
import org.hydrofoil.core.engine.internal.EnginePropertySet;

/**
 * StandardVertex
 * <p>
 * package org.hydrofoil.core.engine
 *
 * @author xie_yh
 * @date 2018/7/2 15:29
 */
public final class EngineVertex extends EngineElement {

    public EngineVertex(GraphElementId elementId, EnginePropertySet propertySet) {
        super(elementId, propertySet);
    }
}
