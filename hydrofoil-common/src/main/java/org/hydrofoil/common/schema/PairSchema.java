package org.hydrofoil.common.schema;

import org.dom4j.Element;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.common.util.XmlUtils;

/**
 * PairSchema
 * <p>
 * package org.hydrofoil.common.schema
 *
 * @author xie_yh
 * @date 2018/7/3 16:09
 */
public final class PairSchema extends SchemaItem{

    private static final String ATTR_PAIR_NAME = "name";
    private static final String ATTR_PAIR_FIELD = "field";

    public PairSchema(){}

    @Override
    void parse(final Element node){
        //basic info
        String name = XmlUtils.attributeStringValue(node,ATTR_PAIR_NAME);
        String fieldname = XmlUtils.attributeStringValue(node,ATTR_PAIR_FIELD);

        ParameterUtils.notBlank(name);
        ParameterUtils.notBlank(fieldname);

        putItem(ATTR_PAIR_NAME,name);
        putItem(ATTR_PAIR_FIELD,fieldname);
    }

    public String getName(){
        return getItem(ATTR_PAIR_NAME);
    }

    public String getField(){
        return getItem(ATTR_PAIR_FIELD);
    }
}
