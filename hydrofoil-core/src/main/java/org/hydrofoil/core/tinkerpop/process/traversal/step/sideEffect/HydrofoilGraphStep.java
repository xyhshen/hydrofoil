package org.hydrofoil.core.tinkerpop.process.traversal.step.sideEffect;

import org.apache.tinkerpop.gremlin.process.traversal.step.HasContainerHolder;
import org.apache.tinkerpop.gremlin.process.traversal.step.map.GraphStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.HasContainer;
import org.apache.tinkerpop.gremlin.structure.Element;

import java.util.List;

/**
 * HydrofoilGraphStep
 * <p>
 * package org.hydrofoil.core.tinkerpop.process.traversal.step.sideEffect
 *
 * @author xie_yh
 * @date 2018/7/25 15:10
 */
public final class HydrofoilGraphStep<S, E extends Element> extends GraphStep<S, E> implements HasContainerHolder {

    public HydrofoilGraphStep(final GraphStep<S, E> originalStep) {
        super(originalStep.getTraversal(), originalStep.getReturnClass(), originalStep.isStartStep(), originalStep.getIds());
    }

    @Override
    public List<HasContainer> getHasContainers() {
        return null;
    }

    @Override
    public void addHasContainer(HasContainer hasContainer) {
        System.out.println(hasContainer);
    }
}
