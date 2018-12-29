package org.hydrofoil.core.tinkerpop.process.traversal.step.sideEffect;

import org.apache.commons.lang3.StringUtils;
import org.apache.tinkerpop.gremlin.process.traversal.Compare;
import org.apache.tinkerpop.gremlin.process.traversal.Contains;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.step.HasContainerHolder;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.HasContainer;
import org.apache.tinkerpop.gremlin.structure.T;
import org.hydrofoil.common.graph.GraphElementType;
import org.hydrofoil.common.graph.QMatch;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.core.engine.management.SchemaManager;
import org.hydrofoil.core.tinkerpop.glue.MultipleCondition;
import org.hydrofoil.core.tinkerpop.glue.TinkerpopElementUtils;
import org.hydrofoil.core.tinkerpop.structure.HydrofoilTinkerpopGraph;

import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

/**
 * HasContainerHelper
 * <p>
 * package org.hydrofoil.core.tinkerpop.process.traversal.step.sideEffect
 *
 * @author xie_yh
 * @date 2018/8/18 14:55
 */
public final class HasContainerHelper {

    static boolean isProcessIdContainer(HasContainer hasContainer){
        return hasContainer.getKey().equals(T.id.getAccessor()) &&
                (hasContainer.getBiPredicate() == Compare.eq || hasContainer.getBiPredicate() == Contains.within);
    }

    private static boolean isUsableHasSpecial(HasContainer hasContainer){
        if(!StringUtils.equalsIgnoreCase(hasContainer.getKey(),
                T.label.getAccessor())){
            return false;
        }
        BiPredicate<?, ?> bp = hasContainer.getBiPredicate();
        return bp == Compare.eq ||
                bp == Compare.neq ||
                bp == Contains.within ||
                bp == Contains.without;
    }

    static boolean isHasContainerQueriable(final List<HasContainer> containers){
        return containers.stream().
                filter(p->!isProcessIdContainer(p)).
                filter(p->!isUsableHasSpecial(p)).
                filter(p->!TinkerpopElementUtils.isPredicateQueriable(p.getKey(),p.getPredicate())).count() == 0;
    }

    static MultipleCondition buildCondition(final Traversal.Admin<?, ?> traversal, final IActionStep actionStep){
        HydrofoilTinkerpopGraph graph = (HydrofoilTinkerpopGraph) DataUtils.
                getOptional(traversal.getGraph());
        MultipleCondition condition = new MultipleCondition();
        condition.setReturnType(actionStep.getReturnType());
        HasContainerHolder holder = (HasContainerHolder) actionStep;
        extractLabels(graph,condition,holder.getHasContainers());
        extractPropertyQuery(graph,condition,holder.getHasContainers());
        return condition;
    }

    @SuppressWarnings("unchecked")
    private static void extractLabels(final HydrofoilTinkerpopGraph graph, final MultipleCondition condition, final List<HasContainer> hasContainers){
        Set<String> elementLabels = DataUtils.newSetWithMaxSize(5);
        SchemaManager schemaManager = graph.getConnector().getManagement().getSchemaManager();
        Set<String> schemaLabels;
        if(condition.getReturnType() == GraphElementType.vertex){
            schemaLabels = schemaManager.getVertexSchemaMap().keySet();
        }else {
            schemaLabels = schemaManager.getEdgeSchemaMap().keySet();
        }

        for(HasContainer hasContainer:hasContainers){
            //T.label is hasLabel
            if(!StringUtils.equalsIgnoreCase(
                    hasContainer.getKey(),
                    T.label.getAccessor())){
                continue;
            }
            /*
            get all label by query condition
             */
            if(hasContainer.getPredicate().getBiPredicate() == Compare.eq){
                elementLabels.add(Objects.toString(hasContainer.getValue(),""));
            }
            if(hasContainer.getPredicate().getBiPredicate() == Compare.neq){
                elementLabels.addAll(getWithoutLabels(schemaLabels, Collections.singletonList(Objects.toString(hasContainer.getValue(),""))));
            }
            if(hasContainer.getPredicate().getBiPredicate() == Contains.within){
                Collection<String> labels = (Collection<String>) hasContainer.getValue();
                elementLabels.addAll(labels);
            }
            if(hasContainer.getPredicate().getBiPredicate() == Contains.without){
                Collection<String> labels = (Collection<String>) hasContainer.getValue();
                elementLabels.addAll(getWithoutLabels(schemaLabels,labels));
            }
        }
        //set to query
        elementLabels.stream().filter(StringUtils::isNotBlank).forEach(label-> condition.getLabels().add(label));
    }
    private static Collection<String> getWithoutLabels(Set<String> schemaLabels,
                                                Collection<String> values){
        return values.stream().filter(p->!schemaLabels.contains(p)).collect(Collectors.toSet());
    }

    private static void extractPropertyQuery(final HydrofoilTinkerpopGraph graph,
                                             final MultipleCondition condition,
                                             final List<HasContainer> hasContainers){
        List<QMatch.Q> fieldQuerys = new ArrayList<>(hasContainers.size());

        for(HasContainer hasContainer:hasContainers){
            if(TinkerpopElementUtils.isSpecialLabelKey(hasContainer.getKey())){
                continue;
            }
            QMatch.Q q = TinkerpopElementUtils.predicateToQuery(hasContainer.getKey(),
                    hasContainer.getPredicate());
            if(q != null){
                fieldQuerys.add(q);
            }
        }

        condition.getFieldQueries().addAll(fieldQuerys);
    }


}
