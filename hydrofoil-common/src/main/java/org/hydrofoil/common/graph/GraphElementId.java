package org.hydrofoil.common.graph;

import org.apache.commons.lang3.StringUtils;
import org.hydrofoil.common.util.collect.ArrayMap;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * GraphElementId
 * <p>
 * package org.hydrofoil.common.graph
 *
 * @author xie_yh
 * @date 2018/7/2 15:48
 */
public class GraphElementId implements Cloneable{

    /**
     * element label
     */
    protected String label;

    /**
     * unique field
     */
    protected Map<String,Object> unique;

    /**
     * element builder
     */
    public static final class GraphElementBuilder{

        private final Map<String,Object> unique;

        private final String label;

        GraphElementBuilder(final String label){
            this.label = label;
            unique = new HashMap<>();
        }

        GraphElementBuilder(final String label,final String name,final Object value){
            this(label);
            unique.put(name,value);
        }

        /**
         * @param name field name
         * @param value value
         * @see GraphElementId#unique
         **/
        public GraphElementBuilder unique(final String name,final Object value) {
            unique.put(name,value);
            return this;
        }

        /**
         * create vertex id
         * @return vertex id
         */
        public GraphVertexId buildVertexId(){
            return new GraphVertexId(label,unique);
        }

        /**
         * create edge id
         * @return edge id
         */
        public GraphEdgeId buildEdgeId(){
            return new GraphEdgeId(label,unique);
        }
    }

    GraphElementId(final String label,final Map<String,Object> unique){
        this.label = label;
        this.unique = new ArrayMap<>(unique);
    }

    /**
     * @return String
     * @see GraphElementId#label
     **/
    public String label() {
        return label;
    }


    /**
     * @return k,v->(label,value)
     * @see GraphElementId#unique
     **/
    public Map<String, Object> unique() {
        return unique;
    }

    @Override
    public int hashCode(){
        return Objects.hashCode(label) ^ Objects.hashCode(unique);
    }

    @Override
    public boolean equals(final Object otherObj){
        if(!(otherObj instanceof GraphElementId)){
            return false;
        }
        GraphElementId otherId = (GraphElementId)otherObj;
        if(!StringUtils.equalsIgnoreCase(otherId.label,label)){
            return false;
        }
        return Objects.equals(unique,otherId.unique);
    }

    /**
     * make vertex id
     * @param label vertex label
     * @param name property label
     * @param value value
     * @return id
     */
    public static GraphVertexId VertexId(final String label,final String name,final Object value){
        return new GraphElementBuilder(label,name,value).buildVertexId();
    }

    /**
     * make edge id
     * @param label edge label
     * @param name property label
     * @param value value
     * @return id
     */
    public static GraphEdgeId EdgeId(final String label,final String name,final Object value){
        return new GraphElementBuilder(label,name,value).buildEdgeId();
    }

    /**
     * create builder
     * @param label element label
     * @return id builder
     */
    public static GraphElementBuilder builder(final String label){
        return new GraphElementBuilder(label);
    }
}
