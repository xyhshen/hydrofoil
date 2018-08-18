package org.hydrofoil.core.tinkerpop.process.traversal.strategy.optimization;

import org.apache.tinkerpop.gremlin.process.traversal.Traversal;

/**
 * HydrofoilVertexStepStrategy
 * <p>
 * package org.hydrofoil.core.tinkerpop.process.traversal.strategy.optimization
 *
 * @author xie_yh
 * @date 2018/8/13 10:47
 */
public final class HydrofoilVertexStepStrategy extends AbstractStepStrategy {

    @Override
    boolean dispatch(Traversal.Admin<?, ?> traversal) {
        return false;
    }
}
