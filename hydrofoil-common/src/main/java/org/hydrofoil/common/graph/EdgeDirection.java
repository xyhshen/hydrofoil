package org.hydrofoil.common.graph;

import org.apache.commons.lang3.StringUtils;

/**
 * GraphVertexId
 * <p>
 * package org.hydrofoil.common.graph
 *
 * @author xie_yh
 * @date 2018/7/2 15:49
 */
public enum EdgeDirection {

    In(0,"in"),
    Out(1,"out"),
    InAndOut(2,"inandout");

    private int id;
    private String name;

    EdgeDirection(int id,String name){
        this.id = id;
        this.name = name;
    }

    public static EdgeDirection nameOf(String name){
        for(EdgeDirection direction:EdgeDirection.values()){
            if(StringUtils.equalsIgnoreCase(name,direction.getName())){
                return direction;
            }
        }
        return InAndOut;
    }

    /**
     * @return $field.TypeName
     * @see EdgeDirection#id
     **/
    public int getId() {
        return id;
    }

    /**
     * @return String
     * @see EdgeDirection#name
     **/
    public String getName() {
        return name;
    }
}
