package org.hydrofoil.core.engine;

import org.hydrofoil.common.graph.GraphElementId;
import org.hydrofoil.common.graph.QMatch;
import org.hydrofoil.common.util.DataUtils;

import java.util.Iterator;

/**
 * GraphQueryRunner
 * <p>
 * package org.hydrofoil.core.engine.query
 *
 * @author xie_yh
 * @date 2018/7/14 9:35
 */
public interface IGraphQueryRunner <E,T extends IGraphQueryRunner> {

    /**
     * query operation type
     */
    public enum OperationType{
        /**
         * data paging
         */
        paging,
        /**
         * total count
         */
        count,
        /**
         * order by
         */
        order
    }


    /**
     * get element
     * @return element
     */
    default E getElement(){
        return DataUtils.iteratorFirst(take());
    }

    /**
     * find result
     * @return result
     */
    Iterator<E> take();

    /**
     * count total
     * @return total
     */
    Long count();

    /**
     * set element id
     * @param elementIds element ids
     * @return this
     */
    T elements(final GraphElementId ...elementIds);

    /**
     * set offset
     * @param offset start pos
     * @return this
     */
    T offset(final Long offset);

    /**
     * set result length
     * @param length length
     * @return this
     */
    T length(final Long length);

    /**
     * set label
     * @param label graph label
     * @return this
     */
    default T label(final String label){
        return labels(label);
    }

    /**
     * set multi label
     * @param labels label array
     * @return this
     */
    T labels(final String ...labels);

    /**
     * query field
     * @param name field name
     * @param value value
     * @return this
     */
    default T field(final String name,final Object value){
        return fields(QMatch.eq(name,value));
    }

    /**
     * query field
     * @param query query set
     * @return this
     */
    T fields(final QMatch.Q ...query);

    /**
     * check operator is feasible
     * @param type oprator type
     * @return result
     */
    boolean operable(OperationType type);

}
