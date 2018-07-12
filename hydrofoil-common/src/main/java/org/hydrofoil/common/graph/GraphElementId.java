package org.hydrofoil.common.graph;

import org.apache.commons.collections4.SetUtils;
import org.apache.commons.lang3.StringUtils;

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
public class GraphElementId {

    /**
     * element label
     */
    private String label;

    /**
     * unique field
     */
    private Map<String,Object> unique;

    GraphElementId(String label){
        this.label = label;
        this.unique = new HashMap<>();
    }

    /**
     * @return String
     * @see GraphElementId#label
     **/
    public String label() {
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
        unique.put(name,value);
        return this;
    }

    @Override
    public int hashCode(){
        return Objects.hash(label,unique.entrySet().toArray());
    }

    @Override
    public boolean equals(Object otherObj){
        if(!(otherObj instanceof GraphElementId)){
            return false;
        }
        GraphElementId otherId = (GraphElementId)otherObj;
        if(!StringUtils.equalsIgnoreCase(otherId.label,label)){
            return false;
        }
        return SetUtils.isEqualSet(unique.entrySet(),otherId.unique.entrySet());
    }
}
