package org.hydrofoil.common.graph;

import org.apache.commons.lang3.StringUtils;
import org.hydrofoil.common.schema.BaseElementSchema;
import org.hydrofoil.common.util.bean.KeyValueEntity;

import java.io.Serializable;
import java.util.Objects;

/**
 * GraphElementId
 * <p>
 * package org.hydrofoil.common.graph
 *
 * @author xie_yh
 * @date 2018/7/2 15:48
 */
public class GraphElementId implements Cloneable,Serializable {

    /**
     * element label
     */
    protected String label;

    /**
     * unique field
     */
    protected KeyValueEntity unique;

    /**
     * element builder
     */
    public static final class GraphElementBuilder{

        private final String label;

        private final KeyValueEntity keyValue;

        GraphElementBuilder(final String label,final KeyValueEntity keyValue){
            this.label = label;
            this.keyValue = keyValue;
        }

        GraphElementBuilder(final String label,final KeyValueEntity keyValue,final String name,final Object value){
            this(label,keyValue);
            keyValue.put(name,value);
        }

        /**
         * @param name field name
         * @param value value
         * @see GraphElementId#unique
         **/
        public GraphElementBuilder unique(final String name,final Object value) {
            keyValue.put(name,value);
            return this;
        }

        /**
         * create vertex id
         * @return vertex id
         */
        public GraphVertexId buildVertexId(){
            return new GraphVertexId(label,keyValue);
        }

        /**
         * create edge id
         * @return edge id
         */
        public GraphEdgeId buildEdgeId(){
            return new GraphEdgeId(label,keyValue);
        }
    }

    GraphElementId(final String label,final KeyValueEntity unique){
        this.label = label;
        this.unique = unique;
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
    public KeyValueEntity unique() {
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
    public static GraphVertexId vertexId(final String label, final KeyValueEntity keyValue, final String name, final Object value){
        return new GraphElementBuilder(label,keyValue,name,value).buildVertexId();
    }

    /**
     * make edge id
     * @param label edge label
     * @param name property label
     * @param value value
     * @return id
     */
    public static GraphEdgeId edgeId(final String label, final KeyValueEntity keyValue, final String name, final Object value){
        return new GraphElementBuilder(label,keyValue,name,value).buildEdgeId();
    }

    /**
     * create builder
     * @param label element label
     * @return id builder
     */
    public static GraphElementBuilder builder(final String label,final KeyValueEntity keyValue){
        return new GraphElementBuilder(label,keyValue);
    }

    /**
     * create builder
     * @param elementSchema element schema
     * @return id builder
     */
    public static GraphElementBuilder builder(BaseElementSchema elementSchema){
        return new GraphElementBuilder(elementSchema.getLabel(),elementSchema.getPrimaryKeysFactory().create());
    }
}
