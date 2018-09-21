package org.hydrofoil.common.schema;

import org.dom4j.Element;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.common.util.XmlUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

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
    private static final String NODE_ELEMENT_LINKS =            "links";
    private static final String NODE_ELEMENT_LINK =            "link";
    private static final String NODE_ELEMENT_PROPERTIES =      "properties";
    private static final String NODE_ELEMENT_PROPERTY =         "property";

    private List<String> primaryKeys;

    AbstractElementSchema(){
        primaryKeys = new ArrayList<>(1);
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
        return getSchemaMap(NODE_ELEMENT_LINKS);
    }

    @Override
    void parse(final Element node){
        //basic info
        String label = XmlUtils.attributeStringValue(node,ATTR_ELEMENT_LABEL);
        String tablename = XmlUtils.attributeStringValue(node,ATTR_ELEMENT_TABLE);

        ParameterUtils.notBlank(label);
        ParameterUtils.notBlank(tablename);

        putItem(ATTR_ELEMENT_LABEL,label);
        putItem(ATTR_ELEMENT_TABLE,tablename);

        // load element properties
        loadProperties(node.element(NODE_ELEMENT_PROPERTIES));
        //load element links
        loadLinks(node.element(NODE_ELEMENT_LINKS));
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

    private void loadLinks(final Element node){
        List<Element> elements = XmlUtils.listElement(node, NODE_ELEMENT_LINK);
        Map<String,LinkSchema> links = DataUtils.newHashMapWithExpectedSize(elements.size());
        for(Element element:elements){
            LinkSchema linkSchema = new LinkSchema();
            linkSchema.read(element);
            links.put(linkSchema.getTable(),linkSchema);
        }
        putSchemaItem(NODE_ELEMENT_LINKS,links);
    }

    public Collection<String> getPrimaryKeys(){
        return primaryKeys;
    }

}
