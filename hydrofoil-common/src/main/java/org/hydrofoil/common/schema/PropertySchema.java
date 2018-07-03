package org.hydrofoil.common.schema;

import org.apache.commons.lang3.BooleanUtils;
import org.dom4j.Element;
import org.hydrofoil.common.util.FieldUtils;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.common.util.XmlUtils;

import java.util.HashMap;
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
    private static final String ATTR_PROPERTY_FIELD = "field";
    private static final String ATTR_PROPERTY_PRIMARY = "primary";

    private static final String ATTR_PROPERTY_TABLE = "table";
    private static final String ATTR_PROPERTY_REF_FIELD = "ref-field";
    private static final String ATTR_PROPERTY_MULTIPLE = "multiple";

    private static final String ATTR_PROPERTY_CHILDREN_ELEMENT = "pair";

    public PropertySchema(){}

    @Override
    void parse(Element node){
        //basic info
        String label = XmlUtils.attributeStringValue(node,ATTR_PROPERTY_LABEL);
        String field = XmlUtils.attributeStringValue(node,ATTR_PROPERTY_FIELD);
        String primary = XmlUtils.attributeStringValue(node,ATTR_PROPERTY_PRIMARY,"false");
        String table = XmlUtils.attributeStringValue(node,ATTR_PROPERTY_TABLE);
        String reffield = XmlUtils.attributeStringValue(node,ATTR_PROPERTY_REF_FIELD);
        String multiple = XmlUtils.attributeStringValue(node,ATTR_PROPERTY_MULTIPLE,"false");

        ParameterUtils.notBlank(label);

        putItem(ATTR_PROPERTY_LABEL,label);
        putItem(ATTR_PROPERTY_FIELD,field);
        putItem(ATTR_PROPERTY_PRIMARY,primary);
        putItem(ATTR_PROPERTY_TABLE,table);
        putItem(ATTR_PROPERTY_REF_FIELD, FieldUtils.toMap(reffield));
        putItem(ATTR_PROPERTY_MULTIPLE,multiple);

        //load children property
        loadChildren(node);
    }

    private void loadChildren(final Element node){
        if(!XmlUtils.hasChildren(node)){
            return;
        }
        Map<String,PairSchema> map = new HashMap<>();
        List<Element> elements = XmlUtils.listElement(node, ATTR_PROPERTY_CHILDREN_ELEMENT);
        for(Element element:elements){
            PairSchema pairSchema = new PairSchema();
            pairSchema.read(element);
            map.put(pairSchema.getName(),pairSchema);
        }
        putSchemaItem(ATTR_PROPERTY_CHILDREN_ELEMENT,map);
    }

    public String getLabel(){
        return getItem(ATTR_PROPERTY_LABEL);
    }

    public String getField(){
        return getItem(ATTR_PROPERTY_FIELD);
    }

    public boolean isPrimary(){
        return BooleanUtils.toBoolean(getItem(ATTR_PROPERTY_PRIMARY));
    }

    public String getTable(){
        return getItem(ATTR_PROPERTY_TABLE);
    }

    public String getReffield(){
        return getItem(ATTR_PROPERTY_FIELD);
    }

    public boolean isMultiple(){
        return BooleanUtils.toBoolean(getItem(ATTR_PROPERTY_MULTIPLE));
    }

}
