package org.hydrofoil.core.tinkerpop.process.traversal.step.sideEffect;

import org.apache.commons.collections.IteratorUtils;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.tinkerpop.gremlin.process.traversal.Step;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.step.HasContainerHolder;
import org.apache.tinkerpop.gremlin.process.traversal.step.filter.HasStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.filter.RangeGlobalStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.map.CountGlobalStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.map.GraphStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.map.NoOpBarrierStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.map.VertexStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.sideEffect.IdentityStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.HasContainer;
import org.apache.tinkerpop.gremlin.process.traversal.util.TraversalHelper;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.core.engine.IGraphQueryRunner;
import org.hydrofoil.core.tinkerpop.glue.MultipleCondition;
import org.hydrofoil.core.tinkerpop.glue.TinkerpopGraphTransit;
import org.hydrofoil.core.tinkerpop.structure.HydrofoilTinkerpopGraph;

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

    private static Step<?,?> getNextExecutableStep(Step<?, ?> currentStep){
        Step<?, ?> step = currentStep.getNextStep();
        while (step instanceof IdentityStep){
            step = step.getNextStep();
        }
        return step;
    }

    @SuppressWarnings("unchecked")
    public static <E> Iterator<E> filter(Iterator<E> iterator, HasContainerHolder holder){
        return IteratorUtils.filteredIterator(iterator,
                p->HasContainer.testAll((Element) p,holder.getHasContainers()));
    }

    private static boolean isAvailableStep(Step step){
        return step instanceof HasStep ||
                step instanceof NoOpBarrierStep ||
                step instanceof IdentityStep;
    }

    private static void assignPagingStep(
            Traversal.Admin<?, ?> traversal,
            IActionStep actionStep
    ){
        MultipleCondition multipleCondition = actionStep.getMultipleCondition();
        Step<?, ?> step = getNextExecutableStep((Step<?, ?>) actionStep);
        //find range step,only global
        if(!(step instanceof RangeGlobalStep)){
            return;
        }
        RangeGlobalStep rangeGlobalStep = (RangeGlobalStep) step;
        if(!HasContainerHelper.isHasContainerQueriable(((HasContainerHolder)actionStep).getHasContainers())){
            return;
        }
        HydrofoilTinkerpopGraph graph = (HydrofoilTinkerpopGraph) DataUtils.
                getOptional(traversal.getGraph());
        if(!TinkerpopGraphTransit.of(graph).checkConditionOperator(multipleCondition,IGraphQueryRunner.OperationType.paging)){
            return;
        }
        //set range
        multipleCondition.setStart(rangeGlobalStep.getLowRange());
        multipleCondition.setLimit(rangeGlobalStep.getHighRange());
        //remove current step
        traversal.removeStep(rangeGlobalStep);
    }

    @SuppressWarnings("unchecked")
    private static void processCountStep(
            Traversal.Admin<?, ?> traversal,
            IActionStep actionStep){
        Step<?, ?> step = getNextExecutableStep((Step<?, ?>) actionStep);
        Step<?,?> currentStep = (Step<?, ?>) actionStep;
        //find count step,only global
        if(!(step instanceof CountGlobalStep) ||
                CollectionUtils.isNotEmpty(currentStep.getLabels())){
            //must filter flow of has label
            return;
        }
        //set placeholder flag
        actionStep.setPlaceholder(true);
        //get global count step
        CountGlobalStep countGlobalStep = (CountGlobalStep) step;
        //remove current step
        Step countStep = new HydrofoilCountStep<>(traversal,actionStep.getMultipleCondition());
        TraversalHelper.replaceStep(countGlobalStep,countStep,traversal);
    }

    @SuppressWarnings("unchecked")
    private static void moveHasContainers(final Traversal.Admin<?, ?> traversal, Step<?, ?> newStep){
        Step<?, ?> currentStep = newStep.getNextStep();
        HasContainerHolder holder = (HasContainerHolder) newStep;
        while(isAvailableStep(currentStep)){
            if(currentStep instanceof HasStep){
                if(newStep instanceof GraphStep){
                    ((HasStep) currentStep).getHasContainers().
                            stream().
                            filter(p->!GraphStep.processHasContainerIds((GraphStep<?, ?>) newStep, (HasContainer) p)).
                            forEach(v->holder.addHasContainer((HasContainer) v));
                }else{
                    ((HasStep) currentStep).getHasContainers().forEach(v->holder.addHasContainer((HasContainer) v));
                }
                currentStep.getLabels().forEach(newStep::addLabel);
                TraversalHelper.copyLabels(currentStep, currentStep.getPreviousStep(), false);
                traversal.removeStep(currentStep);
            }
            currentStep = currentStep.getNextStep();
        }
    }

    @SuppressWarnings("unchecked")
    private static void processActionStep(final Traversal.Admin<?, ?> traversal, final Step originalStep, final IActionStep actionStep){
        //replace has step
        Step newStep = (Step) actionStep;
        TraversalHelper.replaceStep(originalStep,newStep,traversal);
        //move has to new step
        moveHasContainers(traversal,newStep);
        if(!actionStep.hasIdProcess()){
            //build and assign graph query runner
            final MultipleCondition condition = HasContainerHelper.
                    buildCondition(traversal, actionStep);
            actionStep.setMultipleCondition(condition);
            //set paging
            assignPagingStep(traversal,actionStep);
            //set count
            processCountStep(traversal,actionStep);
        }
    }

    @SuppressWarnings("unchecked")
    public static void processGraphStep(final Traversal.Admin<?, ?> traversal,final GraphStep originalStep){
        //not by id way query
        HydrofoilGraphStep<?,?> newGraphStep = new HydrofoilGraphStep<>(originalStep);
        processActionStep(traversal,originalStep,newGraphStep);
    }

    @SuppressWarnings("unchecked")
    public static void processVertexStep(final Traversal.Admin<?, ?> traversal, final VertexStep originalStep){
        HydrofoilVertexStep<?> newVertexStep = new HydrofoilVertexStep<>(originalStep);
        processActionStep(traversal,originalStep,newVertexStep);
    }
}
