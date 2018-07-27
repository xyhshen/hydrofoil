package org.hydrofoil.core.tinkerpop.process.traversal.strategy.optimization;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.tinkerpop.gremlin.process.traversal.Step;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.step.map.GraphStep;
import org.apache.tinkerpop.gremlin.process.traversal.util.TraversalHelper;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.core.tinkerpop.process.traversal.step.sideEffect.HydrofoilGraphStep;

import java.util.stream.Stream;

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
            if(ArrayUtils.isNotEmpty(originalStep.getIds())){
                //if exist vertex or edge' id，then by this way query
                final Object[] ids = Stream.of(originalStep.getIds()).map((rawId)->{
                    Object id = rawId;
                    if(rawId instanceof Element){
                        id = ((Element)rawId).id();
                    }
                    return id;
                }).toArray();
                originalStep.setIteratorSupplier(()->{
                    Graph graph = (Graph) DataUtils.getOptional(originalStep.getTraversal().getGraph());
                    if(originalStep.returnsVertex()){
                        return graph.vertices(ids);
                    }
                    if(originalStep.returnsEdge()){
                        return graph.edges(ids);
                    }
                    return null;
                });
            }else{
                //not by id way query
                HydrofoilGraphStep<?,?> newStep = new HydrofoilGraphStep<>(originalStep);
                TraversalHelper.replaceStep(originalStep,newStep,traversal);
                Step<?, ?> nextStep = newStep.getNextStep();
                /*while(true){
                    nextStep = nextStep.getNextStep();
                }*/
            }
        });
        return true;
    }

    public static HydrofoilGraphStepStrategy instance(){
        return STEP_STRATEGY_INSTANCE;
    }
}
