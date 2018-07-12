package org.hydrofoil.core.standard.internal;

import org.hydrofoil.common.graph.GraphVertexId;
import org.hydrofoil.core.management.Management;
import org.hydrofoil.core.standard.StandardVertex;

import java.util.Iterator;

/**
 * VertexMapper
 * <p>
 * package org.hydrofoil.core.standard.internal
 *
 * @author xie_yh
 * @date 2018/7/10 17:20
 */
class VertexMapper extends AbstractElementMapper<StandardVertex,GraphVertexId,VertexMapper>{

    VertexMapper(Management management) {
        super(management);
    }

    @Override
    Iterator<StandardVertex> list() {
        return null;
    }

    @Override
    Long count() {
        return null;
    }

}
