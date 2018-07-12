package org.hydrofoil.core.standard.query;

import org.hydrofoil.common.graph.GraphElementId;
import org.hydrofoil.common.graph.QMatch;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

/**
 * GraphCondition
 * <p>
 * package org.hydrofoil.core.standard.query
 *
 * @author xie_yh
 * @date 2018/7/2 15:46
 */
@SuppressWarnings("unchecked")
public class GraphCondition<S extends GraphCondition> {

    /**
     * element id
     */
    private Set<GraphElementId> elementIds;

    /**
     * start pos
     */
    private Long start;

    /**
     * length
     */
    private Long limit;

    /**
     * label
     */
    private String label;

    /**
     * property query condition
     */
    private Set<QMatch.Q> propertyQuerySet;

    public GraphCondition(){
        this.elementIds = new HashSet<>();
        this.propertyQuerySet = new HashSet<>();
    }

    public Collection<GraphElementId> ids(){
        return elementIds;
    }

    public S setId(GraphElementId id){
        this.elementIds.add(id);
        return (S) this;
    }

    public String label(){
        return label;
    }

    public S label(String label){
        this.label = label;
        return (S) this;
    }

    public Long start(){
        return start;
    }

    public S start(Long start){
        this.start = start;
        return (S) this;
    }

    public Long limit(){
        return limit;
    }

    public S limit(Long limit){
        this.limit = limit;
        return (S) this;
    }

    /**
     * put property query
     * @param q query cond
     * @return this
     */
    public S putPropertyQuery(QMatch.Q q){
        propertyQuerySet.add(q);
        return (S) this;
    }

    /**
     * @return Q
     * @see GraphCondition#propertyQuerySet
     **/
    public Set<QMatch.Q> getPropertyQuerySet() {
        return propertyQuerySet;
    }
}
