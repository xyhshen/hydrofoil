package org.hydrofoil.common.schema;

import org.apache.commons.lang3.BooleanUtils;
import org.dom4j.Element;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.common.util.XmlUtils;

import java.util.List;
import java.util.Map;

/**
 * PropertySchema
 * <p>
 * package org.hydrofoil.common.schema
 *
 * @author xie_yh
 * @date 2018/7/3 13:49
 */
public final class PropertySchema extends SchemaItem{

    private static final String ATTR_PROPERTY_LABEL = "label";
    private static final String ATTR_PROPERTY_NAME = "name";
    private static final String ATTR_PROPERTY_FIELD = "field";
    private static final String ATTR_PROPERTY_PRIMARY = "primary";

    private static final String ATTR_PROPERTY_LINK_TABLE = "link-table";
    private static final String ATTR_PROPERTY_MULTIPLE = "multiple";

    private static final String NODE_PROPERTY_CHILDREN_ELEMENT = "pair";

    /**
     * is subclass property
     */
    private final boolean subclass;

    public PropertySchema(){
        this.subclass = false;
    }

    public PropertySchema(final boolean subclass){
        this.subclass = subclass;
    }

    @Override
    void parse(Element node){
        //basic info
        String label = XmlUtils.attributeStringValue(node,ATTR_PROPERTY_LABEL);
        String name = XmlUtils.attributeStringValue(node,ATTR_PROPERTY_NAME);
        String field = XmlUtils.attributeStringValue(node,ATTR_PROPERTY_FIELD);
        String primary = XmlUtils.attributeStringValue(node,ATTR_PROPERTY_PRIMARY,"false");
        String linkTable = XmlUtils.attributeStringValue(node,ATTR_PROPERTY_LINK_TABLE);
        String multiple = XmlUtils.attributeStringValue(node,ATTR_PROPERTY_MULTIPLE,"false");

        if(!subclass){
            ParameterUtils.notBlank(label);
            putItem(ATTR_PROPERTY_LABEL,label);
            putItem(ATTR_PROPERTY_PRIMARY,primary);
            putItem(ATTR_PROPERTY_LINK_TABLE,linkTable);
            putItem(ATTR_PROPERTY_MULTIPLE,multiple);
            //load children property
            loadChildren(node);
        }else{
            ParameterUtils.notBlank(name);
            putItem(ATTR_PROPERTY_NAME,name);
        }
        putItem(ATTR_PROPERTY_FIELD,field);
    }

    private void loadChildren(final Element node){
        if(!XmlUtils.hasChildren(node)){
            return;
        }
        List<Element> elements = XmlUtils.listElement(node, NODE_PROPERTY_CHILDREN_ELEMENT);
        Map<String,PropertySchema> map = DataUtils.newHashMapWithExpectedSize(elements.size());
        for(Element element:elements){
            PropertySchema pairSchema = new PropertySchema(true);
            pairSchema.read(element);
            map.put(pairSchema.getName(),pairSchema);
        }
        putSchemaItem(NODE_PROPERTY_CHILDREN_ELEMENT,map);
    }

    public String getLabel(){
        return getItem(ATTR_PROPERTY_LABEL);
    }

    public String getField(){
        return getItem(ATTR_PROPERTY_FIELD);
    }

    public String getName(){
        return getItem(ATTR_PROPERTY_NAME);
    }

    public boolean isPrimary(){
        return BooleanUtils.toBoolean(getItem(ATTR_PROPERTY_PRIMARY));
    }

    public String getLinkTable(){
        return getItem(ATTR_PROPERTY_LINK_TABLE);
    }

    public boolean isMultiple(){
        return BooleanUtils.toBoolean(getItem(ATTR_PROPERTY_MULTIPLE));
    }

    public Map<String,PropertySchema> getChildren(){
        return getSchemaMap(NODE_PROPERTY_CHILDREN_ELEMENT);
    }

}
