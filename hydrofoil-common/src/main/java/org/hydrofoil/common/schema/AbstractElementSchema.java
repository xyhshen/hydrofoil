package org.hydrofoil.common.schema;

import org.dom4j.Element;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.common.util.XmlUtils;

import java.util.HashMap;
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

    private static final String ATTR_ELEMENT_LABEL =            "name";
    private static final String ATTR_ELEMENT_TABLE =            "table";
    private static final String ATTR_ELEMENT_PROPERTIES =      "properties";
    private static final String ATTR_ELEMENT_PROPERTY =         "property";

    AbstractElementSchema(){}

    public String getLabel(){
        return getItem(ATTR_ELEMENT_LABEL);
    }

    public String getTable(){
        return getItem(ATTR_ELEMENT_TABLE);
    }

    public Map<String,PropertySchema> getProperties(){
        return getSchemaMap(ATTR_ELEMENT_PROPERTIES);
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
        loadProperties(node.element(ATTR_ELEMENT_PROPERTIES));
    }

    private void loadProperties(final Element node){
        List<Element> elements = XmlUtils.listElement(node, ATTR_ELEMENT_PROPERTY);
        ParameterUtils.notEmpty(elements);

        Map<String,PropertySchema> properties = new HashMap<>();
        //parse property schema
        for(Element element:elements){
            PropertySchema propertySchema = new PropertySchema();
            propertySchema.read(element);
            properties.put(propertySchema.getLabel(),propertySchema);
        }

        putSchemaItem(ATTR_ELEMENT_PROPERTIES,properties);
    }

}
