package org.hydrofoil.core.tinkerpop.process.traversal.step.sideEffect;

import org.hydrofoil.common.graph.GraphElementType;
import org.hydrofoil.core.tinkerpop.glue.MultipleCondition;

/**
 * IActionStep
 * <p>
 * package org.hydrofoil.core.tinkerpop.process.traversal.step.sideEffect
 *
 * @author xie_yh
 * @date 2018/12/29 14:07
 */
public interface IActionStep {

    /**
     * @return MultipleCondition
     * @see HydrofoilGraphStep#multipleCondition
     **/
    MultipleCondition getMultipleCondition();

    /**
     * @param multipleCondition MultipleCondition
     * @see HydrofoilGraphStep#multipleCondition
     **/
    void setMultipleCondition(MultipleCondition multipleCondition);

    /**
     * has id process step
     * @return result
     */
    boolean hasIdProcess();

    /**
     * get return tyep
     * @return result
     */
    GraphElementType getReturnType();

    /**
     *
     * @return
     */
    boolean isPlaceholder();

    /**
     *
     * @param placeholder
     */
    void setPlaceholder(boolean placeholder);

}
