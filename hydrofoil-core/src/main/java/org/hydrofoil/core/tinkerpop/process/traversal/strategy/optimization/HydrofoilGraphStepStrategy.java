package org.hydrofoil.core.tinkerpop.process.traversal.strategy.optimization;

import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.step.map.GraphStep;
import org.apache.tinkerpop.gremlin.process.traversal.util.TraversalHelper;
import org.hydrofoil.core.tinkerpop.process.traversal.step.sideEffect.StepHelper;

/**
 * HydrofoilGraphStepStrategy
 * <p>
 * package org.hydrofoil.core.tinkerpop.process.traversal.strategy.optimization
 *
 * @author xie_yh
 * @date 2018/7/25 14:53
 */
public final class HydrofoilGraphStepStrategy extends AbstractStepStrategy {

    private static final HydrofoilGraphStepStrategy STEP_STRATEGY_INSTANCE = new HydrofoilGraphStepStrategy();

    @SuppressWarnings("unchecked")
    @Override
    boolean dispatch(Traversal.Admin<?, ?> traversal) {
        TraversalHelper.getStepsOfClass(GraphStep.class,traversal).forEach((originalStep)->{
            StepHelper.processGraphStep(traversal,originalStep);
        });
        return true;
    }

    public static HydrofoilGraphStepStrategy instance(){
        return STEP_STRATEGY_INSTANCE;
    }
}
