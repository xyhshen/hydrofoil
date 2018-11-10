package org.hydrofoil.common.schema;

import org.dom4j.Element;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.common.util.XmlUtils;

import java.util.*;
import java.util.function.BiConsumer;

/**
 * ElementSchema
 * <p>
 * package org.hydrofoil.common.schema
 *
 * @author xie_yh
 * @date 2018/7/3 13:59
 */
public abstract class AbstractElementSchema extends SchemaItem {

    private static final String ATTR_ELEMENT_LABEL =            "label";
    private static final String ATTR_ELEMENT_TABLE =            "table";
    private static final String NODE_ELEMENT_LINK =            "link";
    private static final String NODE_ELEMENT_PROPERTIES =      "properties";
    private static final String NODE_ELEMENT_PROPERTY =         "property";

    private static final List<BiConsumer<Element,SchemaItem>> DEFINES = Arrays.asList(
            SchemaItems.attributeDefine(ATTR_ELEMENT_LABEL,true),
            SchemaItems.attributeDefine(ATTR_ELEMENT_TABLE,true),
            SchemaItems.nodeDefine(NODE_ELEMENT_LINK,LinkSchema.class)
    );

    private List<String> primaryKeys;

    AbstractElementSchema(){
        primaryKeys = new ArrayList<>(1);
    }

    @Override
    void parse(final Element node){
        //basic info
        loadSchema(node,DEFINES);

        // load element properties
        loadProperties(node.element(NODE_ELEMENT_PROPERTIES));
    }

    private void loadProperties(final Element node){
        List<Element> elements = XmlUtils.listElement(node, NODE_ELEMENT_PROPERTY);
        ParameterUtils.notEmpty(elements);

        Map<String,PropertySchema> properties = DataUtils.newHashMapWithExpectedSize(elements.size());
        //parse property schema
        for(Element element:elements){
            PropertySchema propertySchema = new PropertySchema();
            propertySchema.read(element);
            properties.put(propertySchema.getLabel(),propertySchema);
            if(propertySchema.isPrimary()){
                primaryKeys.add(propertySchema.getLabel());
            }
        }
        ParameterUtils.notEmpty(primaryKeys);
        putSchemaItem(NODE_ELEMENT_PROPERTIES,properties);
    }

    public Collection<String> getPrimaryKeys(){
        return primaryKeys;
    }

    public String getLabel(){
        return getItem(ATTR_ELEMENT_LABEL);
    }

    public String getTable(){
        return getItem(ATTR_ELEMENT_TABLE);
    }

    public Map<String,PropertySchema> getProperties(){
        return getSchemaMap(NODE_ELEMENT_PROPERTIES);
    }

    public Map<String,LinkSchema> getLinks(){
        return getSchemaMap(NODE_ELEMENT_LINK);
    }

    @Override
    public String getId(){
        return getLabel();
    }

}
