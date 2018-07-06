package org.hydrofoil.common.graph;

import org.hydrofoil.common.util.bean.FieldPair;

import java.util.Map;

/**
 * GraphElementId
 * <p>
 * package org.hydrofoil.common.graph
 *
 * @author xie_yh
 * @date 2018/7/2 15:48
 */
public class GraphElementId {

    /**
     * element label
     */
    private String label;

    /**
     * unique field
     */
    private Map<String,Object> unique;

    protected GraphElementId(String label){
        this.label = label;
    }

    /**
     * @return String
     * @see GraphElementId#label
     **/
    public String getLabel() {
        return label;
    }


    /**
     * @return Object>
     * @see GraphElementId#unique
     **/
    public Map<String, Object> unique() {
        return unique;
    }

    /**
     * @param name field name
     * @param value value
     * @see GraphElementId#unique
     **/
    public GraphElementId unique(String name,Object value) {
        this.unique = unique;
        return this;
    }
}
