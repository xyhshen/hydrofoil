package org.hydrofoil.core.standard.query;

import org.hydrofoil.common.graph.GraphElementId;

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
}
