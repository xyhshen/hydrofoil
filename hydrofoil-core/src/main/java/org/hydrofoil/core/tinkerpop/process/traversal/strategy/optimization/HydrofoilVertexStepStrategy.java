package org.hydrofoil.core.tinkerpop.process.traversal.strategy.optimization;

import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.step.map.VertexStep;
import org.apache.tinkerpop.gremlin.process.traversal.util.TraversalHelper;
import org.hydrofoil.core.tinkerpop.process.traversal.step.sideEffect.StepHelper;

/**
 * HydrofoilVertexStepStrategy
 * <p>
 * package org.hydrofoil.core.tinkerpop.process.traversal.strategy.optimization
 *
 * @author xie_yh
 * @date 2018/8/13 10:47
 */
public final class HydrofoilVertexStepStrategy extends AbstractStepStrategy {

    private static final HydrofoilVertexStepStrategy STEP_STRATEGY_INSTANCE = new HydrofoilVertexStepStrategy();

    @Override
    boolean dispatch(Traversal.Admin<?, ?> traversal) {
        TraversalHelper.getStepsOfClass(VertexStep.class,traversal).forEach((originalStep)->{
            StepHelper.processVertexStep(traversal,originalStep);
        });
        return true;
    }

    public static HydrofoilVertexStepStrategy instance(){
        return STEP_STRATEGY_INSTANCE;
    }

}
