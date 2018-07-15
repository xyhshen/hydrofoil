package org.hydrofoil.core.standard;

import org.hydrofoil.common.graph.GraphElementId;
import org.hydrofoil.common.graph.QMatch;
import org.hydrofoil.common.util.DataUtils;

import java.util.Iterator;

/**
 * GraphQueryRunner
 * <p>
 * package org.hydrofoil.core.standard.query
 *
 * @author xie_yh
 * @date 2018/7/14 9:35
 */
public interface IGraphQueryRunner <E extends StandardElement,T extends IGraphQueryRunner> {

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
     * set start
     * @param start start pos
     * @return this
     */
    T start(final Long start);

    /**
     * set limit
     * @param limit limit pos
     * @return this
     */
    T limit(final Long limit);

    /**
     * set label
     * @param label graph label
     * @return this
     */
    T label(final String label);

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

}
