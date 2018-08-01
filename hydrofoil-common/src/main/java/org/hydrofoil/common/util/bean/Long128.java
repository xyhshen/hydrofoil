package org.hydrofoil.common.util.bean;

import java.util.Objects;

/**
 * Long128
 * <p>
 * package org.hydrofoil.common.util.bean
 *
 * @author xie_yh
 * @date 2018/7/31 15:11
 */
public final class Long128 extends Number implements Comparable<Long128>{

    private final long loworder;

    private final long highorder;

    public Long128(final long loworder,final long highorder){
        this.loworder = loworder;
        this.highorder = highorder;
    }

    @Override
    public int compareTo(Long128 o) {
        return 0;
    }

    @Override
    public int hashCode(){
        return Objects.hash(highorder,loworder);
    }

    @Override
    public boolean equals(Object otherObj){
        if(!(otherObj instanceof Long128)){
            return false;
        }
        Long128 other = (Long128) otherObj;
        return other.highorder == highorder &&
                other.loworder == loworder;
    }

    @Override
    public int intValue() {
        return 0;
    }

    @Override
    public long longValue() {
        return loworder;
    }

    @Override
    public float floatValue() {
        return 0;
    }

    @Override
    public double doubleValue() {
        return 0;
    }
}
