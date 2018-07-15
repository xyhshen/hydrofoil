package org.hydrofoil.core.standard;

import org.hydrofoil.common.graph.GraphElementId;

import java.util.Map;

/**
 * StandardVertex
 * <p>
 * package org.hydrofoil.core.standard
 *
 * @author xie_yh
 * @date 2018/7/2 15:29
 */
public final class StandardVertex extends StandardElement{

    public StandardVertex(GraphElementId elementId, Map<String, StandardProperty> propertyMap) {
        super(elementId, propertyMap);
    }
}
