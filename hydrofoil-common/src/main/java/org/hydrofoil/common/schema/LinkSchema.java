package org.hydrofoil.common.schema;

import org.dom4j.Element;
import org.hydrofoil.common.util.FieldUtils;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.common.util.XmlUtils;

import java.util.Map;

/**
 * LinkSchema
 * <p>
 * package org.hydrofoil.common.schema
 *
 * @author xie_yh
 * @date 2018/9/3 17:20
 */
public final class LinkSchema extends SchemaItem{

    private static final String ATTR_LINK_TABLE = "table";

    private static final String ATTR_LINK_JOIN_FIELD = "join-field";

    public LinkSchema(){}

    @Override
    void parse(Element node){
        String table = XmlUtils.attributeStringValue(node,ATTR_LINK_TABLE);
        String joinfield = XmlUtils.attributeStringValue(node,ATTR_LINK_JOIN_FIELD);

        ParameterUtils.notBlank(table);
        ParameterUtils.notBlank(joinfield);

        putItem(ATTR_LINK_TABLE,table);
        putItem(ATTR_LINK_JOIN_FIELD, FieldUtils.toMap(joinfield));
    }

    public String getTable(){
        return getItem(ATTR_LINK_TABLE);
    }

    public Map<String,String> getJoinfield(){
        return getItemMap(ATTR_LINK_JOIN_FIELD);
    }

}
