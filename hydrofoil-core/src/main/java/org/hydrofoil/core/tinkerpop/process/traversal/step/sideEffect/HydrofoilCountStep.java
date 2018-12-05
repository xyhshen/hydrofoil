package org.hydrofoil.core.tinkerpop.process.traversal.step.sideEffect;

import org.apache.tinkerpop.gremlin.process.traversal.Operator;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.Traverser;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.ReducingBarrierStep;
import org.apache.tinkerpop.gremlin.process.traversal.traverser.TraverserRequirement;
import org.apache.tinkerpop.gremlin.util.function.ConstantSupplier;
import org.hydrofoil.core.engine.IGraphQueryRunner;

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

    private final IGraphQueryRunner graphQueryRunner;

    @SuppressWarnings("unchecked")
    public HydrofoilCountStep(final Traversal.Admin traversal, final IGraphQueryRunner graphQueryRunner) {
        super(traversal);
        this.graphQueryRunner = graphQueryRunner;
        this.setSeedSupplier(new ConstantSupplier<>(0L));
        this.setReducingBiOperator((BinaryOperator) Operator.sumLong);
    }

    @Override
    public Long projectTraverser(Traverser.Admin<S> traverser) {
        return graphQueryRunner.count();
    }

    @Override
    public Set<TraverserRequirement> getRequirements() {
        return REQUIREMENTS;
    }
}
