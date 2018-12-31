package org.hydrofoil.core.tinkerpop.process.traversal.step.sideEffect;

import org.apache.tinkerpop.gremlin.process.traversal.Operator;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.Traverser;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.ReducingBarrierStep;
import org.apache.tinkerpop.gremlin.process.traversal.traverser.TraverserRequirement;
import org.apache.tinkerpop.gremlin.util.function.ConstantSupplier;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.core.tinkerpop.glue.TinkerpopGraphTransit;
import org.hydrofoil.core.tinkerpop.structure.HydrofoilTinkerpopGraph;

import java.util.EnumSet;
import java.util.Set;
import java.util.function.BinaryOperator;

/**
 * HydrofoilCountStep
 * <p>
 * package org.hydrofoil.core.tinkerpop.process.traversal.step.sideEffect
 *
 * @author xie_yh
 * @date 2018/12/1 11:08
 */
public final class HydrofoilCountStep<S> extends ReducingBarrierStep<S, Long> {

    private static final Set<TraverserRequirement> REQUIREMENTS = EnumSet.of(TraverserRequirement.BULK);

    private final IActionStep previousStep;

    @SuppressWarnings("unchecked")
    HydrofoilCountStep(final Traversal.Admin traversal, final IActionStep previousStep) {
        super(traversal);
        this.previousStep = previousStep;
        this.setSeedSupplier(new ConstantSupplier<>(0L));
        this.setReducingBiOperator((BinaryOperator) Operator.sumLong);
    }

    @Override
    public Long projectTraverser(Traverser.Admin<S> traverser) {
        HydrofoilTinkerpopGraph graph = (HydrofoilTinkerpopGraph) DataUtils.
                getOptional(traversal.getGraph());
        final TinkerpopGraphTransit transit = TinkerpopGraphTransit.of(graph);
        if(previousStep instanceof HydrofoilGraphStep){
            //graph
            return transit.countElement(previousStep.getMultipleCondition());
        }else{
            //vertex edge
            return 0L;
        }
    }

    @Override
    public Set<TraverserRequirement> getRequirements() {
        return REQUIREMENTS;
    }
}
