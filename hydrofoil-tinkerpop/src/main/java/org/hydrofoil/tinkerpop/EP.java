package org.hydrofoil.tinkerpop;

import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.hydrofoil.common.graph.expand.ElementPredicate;

import java.util.function.BiPredicate;

/**
 * EP
 * <p>
 * package org.hydrofoil.core.tinkerpop.glue
 *
 * @author xie_yh
 * @date 2018/7/27 14:07
 */
@SuppressWarnings("unchecked")
public class EP<V> extends P<V> {

    private EP(BiPredicate<V, V> biPredicate, V value) {
        super(biPredicate, value);
    }

    /**
     * prefix query
     * @param value value
     * @return P
     */
    public static <V> P<V> prefix(final V value) {
        return new EP(ElementPredicate.prefix, value);
    }

    /**
     * like query
     * @param value value
     * @return P
     */
    public static <V> P<V> like(final V value) {
        return new EP(ElementPredicate.like, value);
    }
}
