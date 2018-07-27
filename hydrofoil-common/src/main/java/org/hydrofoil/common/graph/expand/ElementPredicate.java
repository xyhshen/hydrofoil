package org.hydrofoil.common.graph.expand;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;
import java.util.function.BiPredicate;

/**
 * ElementPredicate
 * <p>
 * package org.hydrofoil.common.graph.expand
 *
 * @author xie_yh
 * @date 2018/7/27 13:57
 */
public enum ElementPredicate implements BiPredicate<Object, Object> {

    /**
     * like Predicate
     */
    like{
        @Override
        public boolean test(final Object first, final Object second) {
            String a = Objects.toString(first,null);
            String b = Objects.toString(second,null);
            return StringUtils.containsIgnoreCase(a,b);
        }
    }
}
