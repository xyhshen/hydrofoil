package org.hydrofoil.core.tinkerpop.process.traversal.step.sideEffect;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.tinkerpop.gremlin.process.traversal.Step;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.step.HasContainerHolder;
import org.apache.tinkerpop.gremlin.process.traversal.step.filter.HasStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.map.GraphStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.map.NoOpBarrierStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.sideEffect.IdentityStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.HasContainer;
import org.apache.tinkerpop.gremlin.process.traversal.util.TraversalHelper;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.hydrofoil.core.standard.IGraphQueryRunner;

import java.util.Iterator;

/**
 * StepHelper
 * <p>
 * package org.hydrofoil.core.tinkerpop.process.traversal.step.sideEffect
 *
 * @author xie_yh
 * @date 2018/8/14 17:10
 */
public final class StepHelper {

    @SuppressWarnings("unchecked")
    public static <E> Iterator<E> filter(Iterator<E> iterator, HasContainerHolder holder){
        return IteratorUtils.filteredIterator(iterator,
                p->HasContainer.testAll((Element) p,holder.getHasContainers()));
    }

    public static boolean isAvailableStep(Step step){
        return step instanceof HasStep ||
                step instanceof NoOpBarrierStep ||
                step instanceof IdentityStep;
    }

    @SuppressWarnings("unchecked")
    public static void processGraphStep(Traversal.Admin<?, ?> traversal,final GraphStep originalStep){
        //not by id way query
        HydrofoilGraphStep<?,?> newGraphStep = new HydrofoilGraphStep<>(originalStep);
        TraversalHelper.replaceStep(originalStep,newGraphStep,traversal);
        Step<?, ?> currentStep = newGraphStep.getNextStep();
        while(isAvailableStep(currentStep)){
            if(currentStep instanceof HasStep){
                ((HasStep) currentStep).getHasContainers().
                        stream().
                        filter(p->!GraphStep.processHasContainerIds(newGraphStep, (HasContainer) p)).
                        forEach(v->newGraphStep.addHasContainer((HasContainer) v));
                currentStep.getLabels().forEach(newGraphStep::addLabel);
                TraversalHelper.copyLabels(currentStep, currentStep.getPreviousStep(), false);
                traversal.removeStep(currentStep);
            }
            currentStep = currentStep.getNextStep();
        }
        if(ArrayUtils.isEmpty(newGraphStep.getIds())){
            IGraphQueryRunner graphQueryRunner = HasContainerHelper.
                    buildQueryRunner(traversal, newGraphStep);
            newGraphStep.setGraphQueryRunner(graphQueryRunner);
        }
    }
}
