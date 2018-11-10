package org.hydrofoil.common.schema;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

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

    private static final String ATTR_LINK_ONE_TO_MANY = "one-to-many";

    private static final String ATTR_LINK_ONLY_QUERY = "only-query";

    private static final List<BiConsumer<Element,SchemaItem>> DEFINES = Arrays.asList(
            SchemaItems.attributeDefine(ATTR_LINK_TABLE,true),
            SchemaItems.attributeDefine(ATTR_LINK_JOIN_FIELD,true),
            SchemaItems.attributeDefine(ATTR_LINK_ONE_TO_MANY,false,null,"false"),
            SchemaItems.attributeDefine(ATTR_LINK_ONLY_QUERY,false,null,"false")
    );

    public LinkSchema(){}

    @Override
    void parse(Element node){
        //load schema
        loadSchema(node,DEFINES);
    }

    public String getTable(){
        return getItem(ATTR_LINK_TABLE);
    }

    public Map<String,String> getJoinfield(){
        return getItemMap(ATTR_LINK_JOIN_FIELD);
    }

    public boolean isOneToMany(){
        return StringUtils.equalsAnyIgnoreCase(getItem(ATTR_LINK_ONE_TO_MANY),"true");
    }

    public boolean isOnlyQuery(){
        return StringUtils.equalsAnyIgnoreCase(getItem(ATTR_LINK_ONLY_QUERY),"true");
    }

    @Override
    public String getId(){
        return getTable();
    }

}
