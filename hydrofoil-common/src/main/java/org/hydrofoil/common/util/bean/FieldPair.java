package org.hydrofoil.common.util.bean;

import org.apache.commons.lang3.StringUtils;

import java.util.Objects;

/**
 * FieldValue
 * <p>
 * package org.hydrofoil.common.util.bean
 *
 * @author xie_yh
 * @date 2018/7/3 9:15
 */
public class FieldPair {

    /**
     * field name
     */
    private String name;

    /**
     * value
     */
    private Object first;

    /**
     * construction pair
     * @param name name
     * @param first first value
     */
    public FieldPair(final String name,final Object first){
        this.name = name;
        this.first = first;
    }

    /**
     *
     * @return name
     */
    public String name(){
        return name;
    }

    /**
     *
     * @return first value
     */
    public Object first(){
        return first;
    }

    @Override
    public int hashCode(){
        return Objects.hash(name,first);
    }

    @Override
    public boolean equals(Object object){
        if(!(object instanceof FieldPair)){
            return false;
        }
        return StringUtils.equals(name,((FieldPair)object).name);
    }

}
