package org.hydrofoil.common.util.bean;

import org.hydrofoil.common.util.DataUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * FieldPairArray
 * <p>
 * package org.hydrofoil.common.util.bean
 *
 * @author xie_yh
 * @date 2018/11/3 15:11
 */
public final class FieldPairArray extends FieldPair {

    @SuppressWarnings("unchecked")
    public static Collection<FieldPair> toCollection(Object o){
        if(o instanceof FieldPairArray){
            List<FieldPair> rawList = (List<FieldPair>) ((FieldPairArray)o).first();
            return Collections.unmodifiableCollection(rawList);
        }
        return null;
    }

    /**
     * construction pair
     *
     * @param name  name
     */
    public FieldPairArray(String name) {
        super(name, DataUtils.newList());
    }

    @Override
    public FieldPair clone(){
        final Collection<FieldPair> fieldPairs = toCollection(this);
        return new FieldPair(name(),new ArrayList<>(fieldPairs));
    }
}
