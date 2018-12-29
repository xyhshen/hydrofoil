package org.hydrofoil.core.tinkerpop.glue;

import org.hydrofoil.common.graph.GraphElementType;
import org.hydrofoil.common.graph.QMatch;
import org.hydrofoil.common.util.DataUtils;

import java.util.Set;

/**
 * MultipleCondition
 * <p>
 * package org.hydrofoil.core.tinkerpop.glue
 *
 * @author xie_yh
 * @date 2018/12/27 19:56
 */
public final class MultipleCondition {

    private Set<String> labels;

    private Set<QMatch.Q> fieldQueries;

    private Long start;

    private Long limit;

    private GraphElementType returnType;

    public MultipleCondition(){
        labels = DataUtils.newHashSetWithExpectedSize();
        fieldQueries = DataUtils.newHashSetWithExpectedSize();
        start = null;
        limit = null;
    }

    /**
     * @return String>
     * @see MultipleCondition#labels
     **/
    public Set<String> getLabels() {
        return labels;
    }

    /**
     * @return Q>
     * @see MultipleCondition#fieldQueries
     **/
    public Set<QMatch.Q> getFieldQueries() {
        return fieldQueries;
    }

    /**
     * @return Long
     * @see MultipleCondition#start
     **/
    public Long getStart() {
        return start;
    }

    /**
     * @param start Long
     * @see MultipleCondition#start
     **/
    public void setStart(Long start) {
        this.start = start;
    }

    /**
     * @return Long
     * @see MultipleCondition#limit
     **/
    public Long getLimit() {
        return limit;
    }

    /**
     * @param limit Long
     * @see MultipleCondition#limit
     **/
    public void setLimit(Long limit) {
        this.limit = limit;
    }

    /**
     * @return GraphElementType
     * @see MultipleCondition#returnType
     **/
    public GraphElementType getReturnType() {
        return returnType;
    }

    /**
     * @param returnType GraphElementType
     * @see MultipleCondition#returnType
     **/
    public void setReturnType(GraphElementType returnType) {
        this.returnType = returnType;
    }
}
