package org.hydrofoil.core.tinkerpop.process.traversal.strategy.optimization;

import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.TraversalStrategy;
import org.apache.tinkerpop.gremlin.process.traversal.strategy.AbstractTraversalStrategy;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.core.tinkerpop.structure.HydrofoilTinkerpopGraph;

/**
 * AbstractStepStrategy
 * <p>
 * package org.hydrofoil.core.tinkerpop.process.traversal.strategy.optimization
 *
 * @author xie_yh
 * @date 2018/7/27 8:57
 */
abstract class AbstractStepStrategy extends AbstractTraversalStrategy<TraversalStrategy.ProviderOptimizationStrategy> implements TraversalStrategy.ProviderOptimizationStrategy{

    /**
     * judge is HydrofoilTinkerpopGraph's traversal
     * @param traversal traversal
     * @return result
     */
    private static boolean isInHydrofoilGraph(final Traversal.Admin<?, ?> traversal){
        return DataUtils.getOptional(traversal.getGraph()) instanceof HydrofoilTinkerpopGraph;
    }

    @Override
    public void apply(final Traversal.Admin<?, ?> traversal){
        if(!isInHydrofoilGraph(traversal)){
            return;
        }
        //execute apply
        ParameterUtils.checkStateMessage(
                dispatch(traversal),
                "apply failed!");
    }

    /**
     * send apply
     * @param traversal graph traversal
     * @return if true continue execute,else ignore
     */
    abstract boolean dispatch(final Traversal.Admin<?, ?> traversal);

}
