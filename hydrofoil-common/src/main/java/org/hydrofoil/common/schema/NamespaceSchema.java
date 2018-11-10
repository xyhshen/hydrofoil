package org.hydrofoil.common.schema;

import org.dom4j.Element;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * NamespaceSchema
 * <p>
 * package org.hydrofoil.common.schema
 *
 * @author xie_yh
 * @date 2018/10/17 18:40
 */
public final class NamespaceSchema extends SchemaItem {

    private static final String ATTR_NAMESPACE_NAME = "name";

    private static final String ATTR_DATASOURCE_NAME = "datasource";

    private static final String NODE_NAMESPACE_OPTIONS = "options";

    private static final List<BiConsumer<Element,SchemaItem>> DEFINES = Arrays.asList(
            SchemaItems.attributeDefine(ATTR_NAMESPACE_NAME,true),
            SchemaItems.attributeDefine(ATTR_DATASOURCE_NAME,true),
            SchemaItems.nodeDefine(NODE_NAMESPACE_OPTIONS)
    );

    public NamespaceSchema(){}

    @Override
    void parse(final Element node){
        loadSchema(node,DEFINES);
    }

    public String getName(){
        return getItem(ATTR_NAMESPACE_NAME);
    }

    public Map<String,String> getOptionMap() {
        return getItemMap(ATTR_NAMESPACE_NAME);
    }

    public String getDatasourceName(){
        return getItem(ATTR_DATASOURCE_NAME);
    }

    @Override
    public String getId(){
        return getName();
    }
}
