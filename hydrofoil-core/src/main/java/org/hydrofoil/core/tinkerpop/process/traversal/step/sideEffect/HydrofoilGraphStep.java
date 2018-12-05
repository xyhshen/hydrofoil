package org.hydrofoil.core.tinkerpop.process.traversal.step.sideEffect;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.tinkerpop.gremlin.process.traversal.step.HasContainerHolder;
import org.apache.tinkerpop.gremlin.process.traversal.step.map.GraphStep;
import org.apache.tinkerpop.gremlin.process.traversal.step.util.HasContainer;
import org.apache.tinkerpop.gremlin.structure.Element;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.core.engine.IGraphQueryRunner;
import org.hydrofoil.core.engine.query.EdgeGraphQueryRunner;
import org.hydrofoil.core.engine.query.VertexGraphQueryRunner;
import org.hydrofoil.core.tinkerpop.glue.TinkerpopElementUtils;
import org.hydrofoil.core.tinkerpop.glue.TinkerpopGraphTransit;
import org.hydrofoil.core.tinkerpop.structure.HydrofoilTinkerpopGraph;

import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.stream.Stream;

/**
 * HydrofoilGraphStep
 * <p>
 * package org.hydrofoil.core.tinkerpop.process.traversal.step.sideEffect
 *
 * @author xie_yh
 * @date 2018/7/25 15:10
 */
public final class HydrofoilGraphStep<S, E extends Element> extends GraphStep<S, E> implements HasContainerHolder {

    /**
     * Has step Container list
     */
    private List<HasContainer> hasContainers = new LinkedList<>();

    private IGraphQueryRunner graphQueryRunner = null;

    /**
     * is place holder
     */
    private boolean placeholder;

    HydrofoilGraphStep(final GraphStep<S, E> originalStep) {
        super(originalStep.getTraversal(), originalStep.getReturnClass(), originalStep.isStartStep(), originalStep.getIds());
        this.setIteratorSupplier(this::handler);
        this.placeholder = false;
    }

    @SuppressWarnings("unchecked")
    private Iterator<E> handler(){
        Iterator<E> iterator;
        if(placeholder){
            return DataUtils.newCountIterator(1, (E) TinkerpopElementUtils.emptyElement());
        }
        if(ArrayUtils.isNotEmpty(this.getIds())){
            iterator = listElements();
        }else{
            iterator = queryGraph();
        }
        return StepHelper.filter(iterator,this);
    }

    @SuppressWarnings("unchecked")
    private Iterator<E> queryGraph(){
        ParameterUtils.notNull(graphQueryRunner,"graph query runner");
        HydrofoilTinkerpopGraph graph = (HydrofoilTinkerpopGraph)DataUtils.getOptional(traversal.getGraph());
        Iterator<E> iterator = null;
        if(returnsVertex()){
            iterator = (Iterator<E>) TinkerpopGraphTransit.
                    executeQuery(graph,(VertexGraphQueryRunner) graphQueryRunner);
        }
        if(returnsEdge()){
            iterator = (Iterator<E>) TinkerpopGraphTransit.
                    executeQuery(graph,(EdgeGraphQueryRunner) graphQueryRunner);
        }
        return iterator != null?iterator:IteratorUtils.emptyIterator();
    }

    @SuppressWarnings("unchecked")
    private Iterator<E> listElements(){
        //if exist vertex or edge' id，then by this way query
        final Object[] ids = Stream.of(this.getIds()).map((rawId)->{
            Object id = rawId;
            if(rawId instanceof Element){
                id = ((Element)rawId).id();
            }
            return id;
        }).toArray();

        Graph graph = (Graph) DataUtils.getOptional(getTraversal().getGraph());
        Iterator<E> iterator = null;
        if(returnsVertex()){
            iterator = (Iterator<E>) graph.vertices(ids);
        }
        if(returnsEdge()){
            iterator = (Iterator<E>) graph.edges(ids);
        }
        return iterator != null?iterator:IteratorUtils.emptyIterator();
    }

    @Override
    public List<HasContainer> getHasContainers() {
        return Collections.unmodifiableList(hasContainers);
    }

    @Override
    public void addHasContainer(final HasContainer hasContainer) {
        hasContainers.add(hasContainer);
    }

    void setGraphQueryRunner(final IGraphQueryRunner graphQueryRunner){
        this.graphQueryRunner = graphQueryRunner;
    }

    IGraphQueryRunner getGraphQueryRunner(){
        return graphQueryRunner;
    }

    /**
     * @return $field.TypeName
     * @see HydrofoilGraphStep#placeholder
     **/
    public boolean isPlaceholder() {
        return placeholder;
    }

    /**
     * @param placeholder $field.typeName
     * @see HydrofoilGraphStep#placeholder
     **/
    public void setPlaceholder(boolean placeholder) {
        this.placeholder = placeholder;
    }
}
