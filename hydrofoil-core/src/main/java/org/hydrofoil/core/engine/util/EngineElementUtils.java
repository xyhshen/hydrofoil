package org.hydrofoil.core.engine.util;

import org.apache.commons.collections4.MultiValuedMap;
import org.hydrofoil.common.graph.GraphElementId;
import org.hydrofoil.common.util.DataUtils;

import java.util.Collection;

/**
 * SchemaUtils
 * <p>
 * package org.hydrofoil.core.engine.util
 *
 * @author xie_yh
 * @date 2018/10/16 10:58
 */
public final class EngineElementUtils {

    private EngineElementUtils(){}

    public static <E extends GraphElementId> MultiValuedMap<String,E> clusterIdsWithLabel(Collection<E> ids){
        MultiValuedMap<String,E> labelIdsMap = DataUtils.newMultiMapWithMaxSize(5);
        ids.forEach(id->{
            labelIdsMap.put(id.label(),id);
        });
        return labelIdsMap;
    }
}
