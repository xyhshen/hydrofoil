package org.hydrofoil.core.tinkerpop.glue;

import org.apache.commons.lang3.StringUtils;
import org.apache.tinkerpop.gremlin.process.traversal.Compare;
import org.apache.tinkerpop.gremlin.process.traversal.Contains;
import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.util.AndP;
import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.T;
import org.hydrofoil.common.graph.EdgeDirection;
import org.hydrofoil.common.graph.QMatch;

import java.util.Collection;

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

    public static boolean isSpecialLabelKey(String key){
        return StringUtils.equalsAnyIgnoreCase(
                key,
                T.label.getAccessor(),
                T.id.getAccessor(),
                T.value.getAccessor(),
                T.key.getAccessor());
    }

    public static boolean isQueriableKeyLabel(String key){
        if(isSpecialLabelKey(key) &&
                !StringUtils.equalsIgnoreCase(key,T.label.getAccessor())){
            return false;
        }
        return true;
    }

    public static QMatch.Q predicateToQuery(String key,P<?> predicate){
        if(predicate.getBiPredicate() == Compare.eq){
            return QMatch.eq(key,predicate.getValue());
        }
        if(predicate.getBiPredicate() == Contains.within){
            Collection<?> values = (Collection<?>) predicate.getValue();
            return QMatch.in(key,values.toArray());
        }
        if(predicate.getBiPredicate().getClass().equals(AndP.class)){
            Collection<?> values = (Collection<?>) predicate.getValue();
            return QMatch.in(key,values.toArray());
        }
        return null;
    }

}
