package org.hydrofoil.core.tinkerpop.glue;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.hydrofoil.common.graph.EdgeDirection;

/**
 * TinkerpopElementUtils
 * <p>
 * package org.hydrofoil.core.tinkerpop.glue
 *
 * @author xie_yh
 * @date 2018/7/31 14:15
 */
public final class TinkerpopElementUtils {

    public static EdgeDirection toStdDirection(Direction direction){
        if(direction == Direction.IN){
            return EdgeDirection.In;
        }
        if(direction == Direction.OUT){
            return EdgeDirection.Out;
        }
        return EdgeDirection.InAndOut;
    }

}
