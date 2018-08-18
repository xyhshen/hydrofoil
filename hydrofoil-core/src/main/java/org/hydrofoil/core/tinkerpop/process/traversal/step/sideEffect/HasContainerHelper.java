package org.hydrofoil.core.tinkerpop.process.traversal.step.sideEffect;

import org.apache.commons.lang3.StringUtils;
import org.apache.tinkerpop.gremlin.process.traversal.Compare;
import org.apache.tinkerpop.gremlin.process.traversal.Contains;
import org.apache.tinkerpop.gremlin.process.traversal.Traversal;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.HasContainer;
import org.apache.tinkerpop.gremlin.structure.T;
import org.hydrofoil.common.graph.QMatch;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.core.standard.IGraphQueryRunner;
import org.hydrofoil.core.standard.management.SchemaManager;
import org.hydrofoil.core.standard.query.VertexGraphQueryRunner;
import org.hydrofoil.core.tinkerpop.glue.TinkerpopElementUtils;
import org.hydrofoil.core.tinkerpop.structure.HydrofoilGraph;

import java.util.*;
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

    static IGraphQueryRunner buildQueryRunner(final Traversal.Admin<?, ?> traversal,final HydrofoilGraphStep<?,?> graphStep){
        HydrofoilGraph graph = (HydrofoilGraph) DataUtils.
                getOptional(traversal.getGraph());
        IGraphQueryRunner graphQueryRunner;
        if(graphStep.returnsVertex()){
            graphQueryRunner = graph.getConnector().vertices();
        }else{
            graphQueryRunner = graph.getConnector().edges();
        }
        extractLabels(graph,graphQueryRunner,graphStep.getHasContainers());
        extractPropertyQuery(graph,graphQueryRunner,graphStep.getHasContainers());
        return graphQueryRunner;
    }

    @SuppressWarnings("unchecked")
    static void extractLabels(final HydrofoilGraph graph,final IGraphQueryRunner graphQueryRunner,final List<HasContainer> hasContainers){
        Set<String> elementLabels = DataUtils.newSetWithMaxSize(5);
        SchemaManager schemaManager = graph.getConnector().getManagement().getSchemaManager();
        Set<String> schemaLabels;
        if(graphQueryRunner instanceof VertexGraphQueryRunner){
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
        elementLabels.stream().filter(StringUtils::isNotBlank).forEach(graphQueryRunner::label);
    }
    private static Collection<String> getWithoutLabels(Set<String> schemaLabels,
                                                Collection<String> values){
        return values.stream().filter(p->!schemaLabels.contains(p)).collect(Collectors.toSet());
    }

    static void extractPropertyQuery(final HydrofoilGraph graph,
                                     final IGraphQueryRunner graphQueryRunner,
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
        graphQueryRunner.fields(fieldQuerys.toArray(new QMatch.Q[1]));
    }


}
