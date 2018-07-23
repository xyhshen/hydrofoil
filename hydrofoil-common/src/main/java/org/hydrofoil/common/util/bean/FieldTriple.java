package org.hydrofoil.common.util.bean;

import java.util.Objects;

/**
 * FieldTriple
 * <p>
 * package org.hydrofoil.common.util.bean
 *
 * @author xie_yh
 * @date 2018/7/3 9:18
 */
public class FieldTriple extends FieldPair {

    private Object last;

    public FieldTriple(final String name, final Object first,final Object last) {
        super(name, first);
        this.last = last;
    }

    /**
     *
     * @return last
     */
    public Object last(){
        return last;
    }

    @Override
    public int hashCode(){
        return super.hashCode() + Objects.hashCode(last);
    }

    @Override
    public boolean equals(Object object){
        if(!(object instanceof FieldTriple)){
            return false;
        }
        return super.equals(object);
    }

    @SuppressWarnings("MethodDoesntCallSuperMethod")
    @Override
    public FieldTriple clone(){
        return new FieldTriple(name(),first(),last);
    }
}
