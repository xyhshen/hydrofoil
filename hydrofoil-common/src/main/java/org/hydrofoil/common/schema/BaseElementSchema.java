package org.hydrofoil.common.schema;

import org.dom4j.Element;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.common.util.ArgumentUtils;
import org.hydrofoil.common.util.XmlUtils;
import org.hydrofoil.common.util.bean.KeyValueEntity;

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
public abstract class BaseElementSchema extends SchemaItem {

    private static final String ATTR_ELEMENT_LABEL =            "label";
    private static final String ATTR_ELEMENT_TABLE =            "table";
    private static final String NODE_ELEMENT_LINK =            "link";
    private static final String NODE_ELEMENT_LINKS =            "links";
    private static final String NODE_ELEMENT_PROPERTIES =      "properties";
    private static final String NODE_ELEMENT_PROPERTY =         "property";

    private static final List<BiConsumer<Element,SchemaItem>> DEFINES = Arrays.asList(
            SchemaItems.attributeDefine(ATTR_ELEMENT_LABEL,true),
            SchemaItems.attributeDefine(ATTR_ELEMENT_TABLE,true),
            SchemaItems.nodeDefine(NODE_ELEMENT_LINKS,NODE_ELEMENT_LINK,LinkSchema.class)
    );

    private KeyValueEntity.KeyValueEntityFactory primaryKeysFactory;

    private KeyValueEntity.KeyValueEntityFactory primaryKeyFieldsFactory;

    BaseElementSchema(){}

    @Override
    void parse(final Element node){
        //basic info
        loadSchema(node,DEFINES);

        // load element properties
        loadProperties(node.element(NODE_ELEMENT_PROPERTIES));
    }

    private void loadProperties(final Element node){
        List<Element> elements = XmlUtils.listElement(node, NODE_ELEMENT_PROPERTY);
        ArgumentUtils.notEmpty(elements);

        Map<String,PropertySchema> properties = DataUtils.newHashMapWithExpectedSize(elements.size());
        //parse property schema
        List<String> primaryKeys = new ArrayList<>();
        List<String> primaryKeyFields = new ArrayList<>();
        for(Element element:elements){
            PropertySchema propertySchema = new PropertySchema();
            propertySchema.read(element);
            properties.put(propertySchema.getLabel(),propertySchema);
            if(propertySchema.isPrimary()){
                primaryKeys.add(propertySchema.getLabel());
                primaryKeyFields.add(propertySchema.getField());
            }
        }
        ArgumentUtils.notEmpty(primaryKeys);
        putSchemaItem(NODE_ELEMENT_PROPERTIES,properties);
        primaryKeysFactory = KeyValueEntity.createFactory(primaryKeys);
        primaryKeyFieldsFactory = KeyValueEntity.createFactory(primaryKeyFields);
    }

    public Collection<String> getPrimaryKeys(){
        return primaryKeysFactory.keys();
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

    /**
     * @return KeyValueEntityFactory
     * @see BaseElementSchema#primaryKeyFieldsFactory
     **/
    public KeyValueEntity.KeyValueEntityFactory getPrimaryKeyFieldsFactory() {
        return primaryKeyFieldsFactory;
    }

    /**
     * @return KeyValueEntityFactory
     * @see BaseElementSchema#primaryKeysFactory
     **/
    public KeyValueEntity.KeyValueEntityFactory getPrimaryKeysFactory() {
        return primaryKeysFactory;
    }

    @Override
    public String getId(){
        return getLabel();
    }

}
