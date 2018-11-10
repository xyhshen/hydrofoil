package org.hydrofoil.common.schema;

import org.dom4j.Element;
import org.hydrofoil.common.util.XmlUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * DataSourceSchema
 * collect source config shcema
 * <p>
 * package org.hydrofoil.common.schema
 *
 * @author xie_yh
 * @date 2018/6/29 13:37
 */
public final class DataSourceSchema extends SchemaItem{

    private static final String ATTR_DATASOURCE_NAME = "name";
    private static final String ATTR_DATASOURCE_PROVIDER = "provider";

    private static final String NODE_DATASOURCE_CONFIGITEM = "configitem";

    private static final List<BiConsumer<Element,SchemaItem>> DEFINES = Arrays.asList(
            SchemaItems.attributeDefine(ATTR_DATASOURCE_NAME,true),
            SchemaItems.attributeDefine(ATTR_DATASOURCE_PROVIDER,true),
            SchemaItems.nodeDefine(NODE_DATASOURCE_CONFIGITEM)
    );

    public DataSourceSchema(){}

    @Override
    void parse(final Element node){
        //base info
        loadSchema(node,DEFINES);
        //load option
        loadChildrenOption(node);
    }

    private void loadChildrenOption(final Element node){
        List<Element> elements = XmlUtils.listElement(node);
        for(Element element:elements){
            if(!XmlUtils.hasChildren(element)){
                continue;
            }
            Map<String,String> optionMap = XmlUtils.toStringMap(element);
            putItem(element.getName(),optionMap);
        }
    }

    /**
     * get datasource name
     * @return name
     */
    public String getDatasourceName(){
        return getItem(ATTR_DATASOURCE_NAME);
    }

    /**
     * get datasource provider full name
     * @return provider name
     */
    public String getProvider(){
        return getItem(ATTR_DATASOURCE_PROVIDER);
    }

    /**
     * get all config item
     * @return config item map
     */
    public Map<String,String> getConfigItems(){
        return getItemMap(NODE_DATASOURCE_CONFIGITEM);
    }

    @Override
    public String getId(){
        return getDatasourceName();
    }
}
