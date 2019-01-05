package org.hydrofoil.core.tinkerpop.process.traversal.step.sideEffect;

import org.apache.commons.collections4.MultiValuedMap;
import org.apache.tinkerpop.gremlin.process.traversal.Traverser;
import org.apache.tinkerpop.gremlin.process.traversal.step.HasContainerHolder;
import org.apache.tinkerpop.gremlin.process.traversal.step.map.VertexStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.HasContainer;
import org.apache.tinkerpop.gremlin.process.traversal.util.FastNoSuchElementException;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.hydrofoil.common.graph.GraphElementType;
import org.hydrofoil.common.graph.GraphVertexId;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.common.util.ArgumentUtils;
import org.hydrofoil.core.tinkerpop.glue.MultipleCondition;
import org.hydrofoil.core.tinkerpop.glue.TinkerpopGraphTransit;
import org.hydrofoil.core.tinkerpop.structure.HydrofoilTinkerpopGraph;
import org.hydrofoil.core.tinkerpop.structure.HydrofoilVertex;

import java.util.*;

/**
 * HydrofoilVertexStep
 * <p>
 * package org.hydrofoil.core.tinkerpop.process.traversal.step.sideEffect
 *
 * @author xie_yh
 * @date 2018/12/27 17:05
 */
public final class HydrofoilVertexStep<E extends Element> extends VertexStep<E> implements HasContainerHolder,IActionStep {

    /**
     * Has step Container list
     */
    private List<HasContainer> hasContainers = new LinkedList<>();

    /**
     * already expand
     */
    private boolean expanded = false;

    private MultipleCondition multipleCondition;

    /**
     * is place holder
     */
    private boolean placeholder;

    /**
     * expand element's
     */
    private MultiValuedMap<GraphVertexId,Element> expandElements = DataUtils.newMultiMapWithMaxSize(10);

    HydrofoilVertexStep(final VertexStep<E> originalStep) {
        super(originalStep.getTraversal(), originalStep.getReturnClass(), originalStep.getDirection(), originalStep.getEdgeLabels());
        originalStep.getLabels().forEach(this::addLabel);
    }

    @SuppressWarnings("unchecked")
    private void expand(){
        ArgumentUtils.mustTrueException(starts.hasNext(),"expand failed", FastNoSuchElementException.instance());
        final Set<HydrofoilVertex> vertexSet = DataUtils.newHashSetWithExpectedSize();
        final List<Traverser.Admin<Vertex>> backStarts = new ArrayList<>();
        starts.forEachRemaining(v -> {
            vertexSet.add((HydrofoilVertex) v.get());
            backStarts.add(v);
        });
        //must save starts
        starts.add(backStarts.iterator());
        HydrofoilTinkerpopGraph graph = (HydrofoilTinkerpopGraph) DataUtils.
                getOptional(traversal.getGraph());
        final TinkerpopGraphTransit transit = TinkerpopGraphTransit.of(graph);
        if(returnsEdge()){
            expandElements = (MultiValuedMap)transit.
                    listEdgesByVerticesFlatMap(
                    vertexSet,
                    getDirection(),
                    multipleCondition
            );
        }
        if(returnsVertex()){
            expandElements = (MultiValuedMap) transit.listEdgeVerticesByVertices(
                    vertexSet,
                    getDirection(),
                    multipleCondition,
                    true
            );
        }

        expanded = true;
    }

    @Override
    protected Traverser.Admin<E> processNextStart() {
        if(!expanded){
            expand();
        }
        return super.processNextStart();
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Iterator<E> flatMap(final Traverser.Admin<Vertex> traverser) {
        final Vertex vertex = traverser.get();
        if(vertex == null){
            return (Iterator<E>) expandElements.values();
        }
        HydrofoilVertex v = (HydrofoilVertex) vertex;
        Iterator<E> iterator = (Iterator<E>) expandElements.get((GraphVertexId) v.standard().elementId()).iterator();
        return StepHelper.filter(iterator,this);
    }

    @Override
    public List<HasContainer> getHasContainers() {
        return hasContainers;
    }

    @Override
    public void addHasContainer(HasContainer hasContainer) {
        hasContainers.add(hasContainer);
    }

    /**
     * @return MultipleCondition
     * @see HydrofoilVertexStep#multipleCondition
     **/
    @Override
    public MultipleCondition getMultipleCondition() {
        return multipleCondition;
    }

    /**
     * @param multipleCondition MultipleCondition
     * @see HydrofoilVertexStep#multipleCondition
     **/
    @Override
    public void setMultipleCondition(MultipleCondition multipleCondition) {
        this.multipleCondition = multipleCondition;
    }

    @Override
    public boolean hasIdProcess() {
        return hasContainers.stream().filter(HasContainerHelper::isProcessIdContainer).count() > 0;
    }

    @Override
    public GraphElementType getReturnType() {
        if(returnsVertex()){
            return GraphElementType.vertex;
        }
        if(returnsEdge()){
            return GraphElementType.edge;
        }
        return GraphElementType.none;
    }

    @Override
    public boolean isPlaceholder() {
        return placeholder;
    }

    @Override
    public void setPlaceholder(boolean placeholder) {
        this.placeholder = placeholder;
    }
}
