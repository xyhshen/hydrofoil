package org.hydrofoil.core.standard.internal;

import org.hydrofoil.common.graph.GraphElementId;
import org.hydrofoil.core.management.Management;

import java.util.Iterator;

/**
 * ElementMapper
 * <p>
 * package org.hydrofoil.core.standard.internal
 *
 * @author xie_yh
 * @date 2018/7/11 16:27
 */
@SuppressWarnings("unchecked")
abstract class AbstractElementMapper <E,EID extends GraphElementId,M extends AbstractElementMapper> {

    protected Management management;

    protected String label;

    protected Long start;

    protected Long limit;

    protected EID[] elementIds;

    AbstractElementMapper(Management management){
        this.management = management;
    }

    /**
     * get multi element
     * @param elementIds element id
     * @return this
     */
    M elements(EID ... elementIds){
        this.elementIds = elementIds;
        return (M) this;
    }

    M start(Long start){
        this.start = start;
        return (M) this;
    }

    M limit(Long limit){
        this.limit = limit;
        return (M) this;
    }

    M label(String label){
        this.label = label;
        return (M) this;
    }

    /**
     * list elements
     * @return result
     */
    abstract Iterator<E> list();

    /**
     * count
     * @return total
     */
    abstract Long count();

}
