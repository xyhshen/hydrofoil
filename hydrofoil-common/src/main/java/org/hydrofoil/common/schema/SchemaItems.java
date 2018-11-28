package org.hydrofoil.common.schema;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.common.util.LangUtils;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.common.util.XmlUtils;

import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;
import java.util.function.Predicate;

/**
 * SchemaItems
 * <p>
 * package org.hydrofoil.common.schema
 *
 * @author xie_yh
 * @date 2018/10/20 12:10
 */
public final class SchemaItems {

    private SchemaItems(){}

    static AttributeDefine attributeDefine(final String name){
        return attributeDefine(name,false,null);
    }

    static AttributeDefine attributeDefine(final String name,final boolean required){
        return attributeDefine(name,required,null);
    }

    static AttributeDefine attributeDefine(final String name,final boolean required,
                                           final Predicate checkPredicate){
        return new AttributeDefine(name,required,checkPredicate,null);
    }

    static AttributeDefine attributeDefine(final String name,final boolean required,
                                           final Predicate checkPredicate,String defaultValue){
        return new AttributeDefine(name,required,checkPredicate,defaultValue);
    }

    static NodeDefine nodeDefine(final String name){
        return nodeDefine(name,Map.class);
    }
    static NodeDefine nodeDefine(final String name,final Class<?> nodeClass){
        return new NodeDefine(null,name,nodeClass);
    }

    static NodeDefine nodeDefine(final String parent,final String name,final Class<?> nodeClass){
        return new NodeDefine(parent,name,nodeClass);
    }
}

/**
 * attribute define
 */
class AttributeDefine implements BiConsumer<Element,SchemaItem> {

    /**
     * attr name
     */
    private final String name;

    /**
     * not blank
     */
    private final boolean required;

    /**
     * check
     */
    private final Predicate checkPredicate;

    /**
     * default value
     */
    private final String defaultValue;

    AttributeDefine(final String name, final boolean required, final Predicate checkPredicate,final String defaultValue){
        this.name = name;
        this.required = required;
        this.checkPredicate = checkPredicate;
        this.defaultValue = defaultValue;
    }

    /**
     * @return String
     * @see AttributeDefine#name
     **/
    public String getName() {
        return name;
    }

    /**
     * @return $field.TypeName
     * @see AttributeDefine#required
     **/
    public boolean isRequired() {
        return required;
    }

    /**
     * @return Predicate
     * @see AttributeDefine#checkPredicate
     **/
    public Predicate getCheckPredicate() {
        return checkPredicate;
    }

    /**
     * check attribute value
     * @param value value
     */
    @SuppressWarnings("unchecked")
    private void checkValue(final String value){
        if(required){
            ParameterUtils.notBlank(value,name);
        }
        if(checkPredicate != null && StringUtils.isNotBlank(value)){
            ParameterUtils.mustTrueMessage(checkPredicate.test(value),"check value failed:" + name);
        }
    }

    @Override
    public void accept(Element node,SchemaItem schemaItem) {
        //get attribute
        String value = XmlUtils.attributeStringValue(node,getName());
        //check and set
        checkValue(value);
        if(StringUtils.isBlank(value)){
            value = defaultValue;
        }
        schemaItem.putItem(getName(),value);
    }

    /**
     * @return String
     * @see AttributeDefine#defaultValue
     **/
    public String getDefaultValue() {
        return defaultValue;
    }
}

class NodeDefine implements BiConsumer<Element,SchemaItem>{

    /**
     * parent node
     */
    private final String parent;

    /**
     * node name
     */
    private final String name;

    /**
     * node class
     */
    private final Class<?> nodeClass;

    NodeDefine(final String parent,final String name,final Class<?> nodeClass){
        this.parent = parent;
        this.name = name;
        this.nodeClass = nodeClass;
    }

    /**
     * @return String
     * @see NodeDefine#name
     **/
    public String getName() {
        return name;
    }

    /**
     * @return Class<?>
     * @see NodeDefine#nodeClass
     **/
    public Class<?> getNodeClass() {
        return nodeClass;
    }

    @Override
    public void accept(Element node, SchemaItem schemaItem) {
        if(nodeClass.isAssignableFrom(Map.class)){
            loadMap(node,schemaItem);
        }else{
            loadSchema(node,schemaItem);
        }
    }

    /**
     * load map
     * @param node node
     * @param schemaItem current schema
     * */
    private void loadMap(Element node, SchemaItem schemaItem){
        List<Element> elements = XmlUtils.listElement(node);
        for(Element element:elements){
            if(!XmlUtils.hasChildren(element)){
                continue;
            }
            Map<String,String> stringMap = XmlUtils.toStringMap(element);
            schemaItem.putItem(element.getName(),stringMap);
        }
    }

    /**
     * load node schema
     * @param node node
     * @param schemaItem schema
     */
    private void loadSchema(Element node, final SchemaItem schemaItem){
        if(StringUtils.isNotBlank(parent)){
            node = node.element(parent);
        }
        List<Element> elements = XmlUtils.listElement(node, getName());
        Map<String,SchemaItem> schemaMap = DataUtils.newHashMapWithExpectedSize(elements.size());
        for(Element element:elements){
            SchemaItem schema = LangUtils.newInstance(getNodeClass());
            ParameterUtils.notNull(schema);
            schema.read(element);
            schemaMap.put(schema.getId(),schema);
        }
        schemaItem.putSchemaItem(getName(),schemaMap);
    }

    /**
     * @return String
     * @see NodeDefine#parent
     **/
    public String getParent() {
        return parent;
    }
}