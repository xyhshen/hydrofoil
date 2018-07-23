package org.hydrofoil.common.graph;

import org.hydrofoil.common.util.bean.FieldPair;
import org.hydrofoil.common.util.bean.FieldTriple;

import java.util.Arrays;
import java.util.Objects;

/**
 * QMatch
 * <p>
 * package org.hydrofoil.common.graph
 *
 * @author xie_yh
 * @date 2018/7/3 9:41
 */
public class QMatch {

    public enum QType{
        eq,
        like,
        between,
        in
    }

    public final static class Q implements Cloneable{

        /**
         * query type
         */
        private final QType type;

        /**
         * use not
         */
        private boolean not;

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

        public boolean isNot(){
            return not;
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

        @SuppressWarnings("MethodDoesntCallSuperMethod")
        @Override
        public Q clone(){
            Q query = new Q(this.type);
            query.pair = pair.clone();
            query.not = not;
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

}
