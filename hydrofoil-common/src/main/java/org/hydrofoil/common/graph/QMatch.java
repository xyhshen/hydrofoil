package org.hydrofoil.common.graph;

import org.hydrofoil.common.util.bean.FieldPair;
import org.hydrofoil.common.util.bean.FieldTriple;
import org.hydrofoil.common.util.bean.KeyValueEntity;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 * QMatch
 * <p>
 * package org.hydrofoil.common.graph
 *
 * @author xie_yh
 * @date 2018/7/3 9:41
 */
public class QMatch{

    public enum QType{
        /**
         * equal ==
         */
        eq,
        /**
         * like %*%
         */
        like,
        /**
         * like *%
         */
        prefix,
        /**
         *  greater than >
         */
        gt,
        /**
         * greater than equal >=
         */
        gte,
        /**
         * less than <
         */
        lt,
        /**
         * less than equal <=
         */
        lte,
        /**
         * between(a,b)
         */
        between,
        /**
         * in(a,b,c,d.....)
         */
        in,

        /**
         * key(a,b,c,d....),every item like(x:value,y:value) and need priority as a query condition
         */
        key,

        /**
         * Q1 and Q2
         */
        and
    }

    public static class Q implements Cloneable{

        /**
         * query type
         */
        private final QType type;

        /**
         * field value
         */
        private FieldPair pair;

        Q(QType type){
            this.type = type;
        }

        public QType type(){
            return type;
        }

        /**
         * get pair or triple
         * @return key value
         */
        public FieldPair pair(){
            return pair;
        }

        Q pair(final FieldPair pair){
            this.pair = pair;
            return this;
        }

        public Q[] subquery(){
            if(pair instanceof FieldTriple){
                FieldTriple triple = (FieldTriple) pair;
                return new Q[]{(Q) triple.first(), (Q) triple.last()};
            }
            return new Q[]{(Q) pair.first()};
        }

        @Override
        public int hashCode(){
            return Objects.hash(pair,type);
        }

        @Override
        public boolean equals(Object obj){
            if(obj instanceof Q){
                return false;
            }
            if(((Q)obj).type != type){
                return false;
            }
            return Objects.equals(this,obj);
        }

        @Override
        public Q clone(){
            Q query = new Q(this.type);
            query.pair = pair.clone();
            return query;
        }
    }

    /**
     * create equal match
     * @param name field name
     * @param value field value
     * @return query match
     */
    public static Q eq(String name,Object value){
        return new Q(QType.eq).pair(new FieldPair(name,value));
    }

    /**
     * create like match
     * @param name field name
     * @param value field value
     * @return query match
     */
    public static Q like(String name,Object value){
        return new Q(QType.like).pair(new FieldPair(name,value));
    }

    /**
     * create prefix match
     * @param name field name
     * @param value field value
     * @return query match
     */
    public static Q prefix(String name,Object value){
        return new Q(QType.prefix).pair(new FieldPair(name,value));
    }

    /**
     * create gt match
     * @param name name
     * @param value value
     * @return query match
     */
    public static Q gt(String name,Object value){
        return new Q(QType.gt).pair(new FieldPair(name,value));
    }

    /**
     * create gte match
     * @param name name
     * @param value value
     * @return query match
     */
    public static Q gte(String name,Object value){
        return new Q(QType.gte).pair(new FieldPair(name,value));
    }

    public static Q lt(String name,Object value){
        return new Q(QType.lt).pair(new FieldPair(name,value));
    }

    public static Q lte(String name,Object value){
        return new Q(QType.lte).pair(new FieldPair(name,value));
    }

    /**
     * create between match
     * @param name field name
     * @param first field value
     * @param last field value
     * @return query match
     */
    public static Q between(String name,Object first,Object last){
        return new Q(QType.between).pair(new FieldTriple(name,first,last));
    }

    /**
     * create in match
     * @param name field name
     * @param values field values
     * @return query match
     */
    public static Q in(String name,Object ...values){
        return new Q(QType.in).pair(new FieldPair(name, Arrays.asList(values)));
    }

    /**
     *
     * @param values
     * @return
     */
    public static Q key(final Collection<KeyValueEntity> values){
        return new Q(QType.key).pair(new FieldPair("key", values));
    }

    /**
     * and match
     * @param left left query
     * @param right right query
     * @return query match
     */
    public static Q and(Q left,Q right){
        return new Q(QType.and).pair(new FieldTriple("and",left,right));
    }

}
