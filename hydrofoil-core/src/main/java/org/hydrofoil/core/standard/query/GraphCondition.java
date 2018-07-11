package org.hydrofoil.core.standard.query;

import org.hydrofoil.common.graph.GraphElementId;
import org.hydrofoil.common.graph.QMatch;

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
    private GraphElementId elementId;

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

    private Set<QMatch.Q> propertyQueryMap;

    public GraphCondition(){
        this.propertyQueryMap = new HashSet<>();
    }

    public GraphElementId id(){
        return elementId;
    }

    public S setId(GraphElementId id){
        this.elementId = id;
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
    public S propertyMap(QMatch.Q q){
        propertyQueryMap.add(q);
        return (S) this;
    }

    /**
     * @return Q>
     * @see GraphCondition#propertyQueryMap
     **/
    public Set<QMatch.Q> getPropertyQueryMap() {
        return propertyQueryMap;
    }
}
