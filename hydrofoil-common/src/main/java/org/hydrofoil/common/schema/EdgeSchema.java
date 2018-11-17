package org.hydrofoil.common.schema;

import org.dom4j.Element;
import org.hydrofoil.common.util.FieldUtils;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.common.util.XmlUtils;

import java.util.Map;

/**
 * EdgeSchema
 * <p>
 * package org.hydrofoil.common.schema
 *
 * @author xie_yh
 * @date 2018/7/3 16:22
 */
public final class EdgeSchema extends BaseElementSchema {

    private static final String NODE_EDGE_SOURCE_ELEMENT = "source";
    private static final String NODE_EDGE_TARGET_ELEMENT = "target";

    private static final String ATTR_EDGE_VERTEX_LABEL = "label";
    private static final String ATTR_EDGE_VERTEX_FIELD = "field";

    public EdgeSchema(){}

    @Override
    void parse(final Element node){
        super.parse(node);
        //parse edge info
        Element source = node.element(NODE_EDGE_SOURCE_ELEMENT);
        Element target = node.element(NODE_EDGE_TARGET_ELEMENT);

        ParameterUtils.notNull(source);
        ParameterUtils.notNull(target);

        String sourceLabel = XmlUtils.attributeStringValue(source,ATTR_EDGE_VERTEX_LABEL);
        Map<String, String> sourceField = FieldUtils.toMap(XmlUtils.attributeStringValue(source,ATTR_EDGE_VERTEX_FIELD));
        String targetLabel = XmlUtils.attributeStringValue(target,ATTR_EDGE_VERTEX_LABEL);
        Map<String, String> targetField = FieldUtils.toMap(XmlUtils.attributeStringValue(target,ATTR_EDGE_VERTEX_FIELD));

        ParameterUtils.notBlank(sourceLabel);
        ParameterUtils.mustTrue(!sourceField.isEmpty());
        ParameterUtils.notBlank(targetLabel);
        ParameterUtils.mustTrue(!targetField.isEmpty());

        putItem("source-" + ATTR_EDGE_VERTEX_LABEL,sourceLabel);
        putItem("source-" + ATTR_EDGE_VERTEX_FIELD,sourceField);
        putItem("target-" + ATTR_EDGE_VERTEX_LABEL,targetLabel);
        putItem("target-" + ATTR_EDGE_VERTEX_FIELD,targetField);
    }

    /**
     * get source vertex label
     * @return label
     */
    public String getSourceLabel(){
        return getItem("source-" + ATTR_EDGE_VERTEX_LABEL);
    }

    /**
     * get source vertex field
     * @return k,v->(vertex property label,table field name)
     */
    public Map<String,String> getSourceField(){
        return getItemMap("source-" + ATTR_EDGE_VERTEX_FIELD);
    }

    /**
     * get target vertex label
     * @return label
     */
    public String getTargetLabel(){
        return getItem("target-" + ATTR_EDGE_VERTEX_LABEL);
    }

    /**
     * get target vertex field
     * @return k,v->(vertex property label,table field name)
     */
    public Map<String,String> getTargetField(){
        return getItemMap("target-" + ATTR_EDGE_VERTEX_FIELD);
    }

}
