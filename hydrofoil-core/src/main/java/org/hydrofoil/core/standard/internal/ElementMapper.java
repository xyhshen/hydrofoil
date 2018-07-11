package org.hydrofoil.core.standard.internal;

import org.hydrofoil.core.management.Management;

/**
 * ElementMapper
 * <p>
 * package org.hydrofoil.core.standard.internal
 *
 * @author xie_yh
 * @date 2018/7/11 16:27
 */
abstract class AbstractElementMapper <E,EID> {

    private Management management;

    AbstractElementMapper(Management management){
        this.management = management;
    }

    abstract E getElement(EID elementId);

}
