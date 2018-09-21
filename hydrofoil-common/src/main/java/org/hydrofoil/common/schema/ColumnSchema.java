package org.hydrofoil.common.schema;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.hydrofoil.common.graph.GraphProperty;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.common.util.XmlUtils;

/**
 * ColumnSchema
 * <p>
 * package org.hydrofoil.common.schema
 *
 * @author xie_yh
 * @date 2018/8/17 16:15
 */
public class ColumnSchema extends SchemaItem{

    private static final String ATTR_COLUMN_NAME = "name";
    private static final String ATTR_COLUMN_INDEX_TYPE = "index-type";
    private static final String ATTR_COLUMN_ACCEPT_TYPE = "accept-type";
    private static final String ATTR_COLUMN_RAW_TYPE = "raw-type";
    private static final String ATTR_COLUMN_RAW_FORMAT = "raw-format";
    private static final String ATTR_COLUMN_RAW_ENCODE = "raw-encode";

    /**
     * index type define
     */
    public static final String COLUMN_INDEX_TYPE_NORMAL = "normal";
    public static final String COLUMN_INDEX_TYPE_TEXT = "text";

    /**
     * column data accept type
     */
    private GraphProperty.PropertyType acceptType;

    public ColumnSchema(){}

    @Override
    void parse(final Element element){
        String name = XmlUtils.attributeStringValue(element,ATTR_COLUMN_NAME);
        String indexType = XmlUtils.attributeStringValue(element,ATTR_COLUMN_INDEX_TYPE,"");
        String acceptType = XmlUtils.attributeStringValue(element,ATTR_COLUMN_ACCEPT_TYPE, GraphProperty.PropertyType.String.typeName());
        String rawType = XmlUtils.attributeStringValue(element,ATTR_COLUMN_RAW_TYPE,"");
        String rawFormat = XmlUtils.attributeStringValue(element,ATTR_COLUMN_RAW_FORMAT,"");
        String rawEncode = XmlUtils.attributeStringValue(element,ATTR_COLUMN_RAW_ENCODE,"utf-8");

        ParameterUtils.notBlank(name);
        ParameterUtils.checkValueNullable(indexType,COLUMN_INDEX_TYPE_NORMAL,
                COLUMN_INDEX_TYPE_TEXT);
        ParameterUtils.checkValueIn(acceptType, GraphProperty.PropertyType.names());

        putItem(ATTR_COLUMN_NAME,name);
        putItem(ATTR_COLUMN_INDEX_TYPE,indexType);
        putItem(ATTR_COLUMN_RAW_TYPE,rawType);
        putItem(ATTR_COLUMN_RAW_FORMAT,rawFormat);
        putItem(ATTR_COLUMN_RAW_ENCODE,rawEncode);

        //set accept type
        this.acceptType = GraphProperty.PropertyType.ofTypeName(acceptType);
    }

    /**
     * get column name
     * @return column name
     */
    public String getColumnName(){
        return getItem(ATTR_COLUMN_NAME);
    }

    /**
     * get index type
     * @return index type
     */
    public String getIndexType(){
        return getItem(ATTR_COLUMN_INDEX_TYPE);
    }

    public boolean isSupportedNormalIndex(){
        return StringUtils.equalsIgnoreCase(getIndexType(),
                COLUMN_INDEX_TYPE_NORMAL);
    }

    public boolean isSupportedTextIndex(){
        return StringUtils.equalsIgnoreCase(getIndexType(),
                COLUMN_INDEX_TYPE_TEXT);
    }

    public GraphProperty.PropertyType getAcceptType(){
        return acceptType;
    }

    public String getRawType(){
        return getItem(ATTR_COLUMN_RAW_TYPE);
    }

    public String getRawFormat(){
        return getItem(ATTR_COLUMN_RAW_FORMAT);
    }

    public String getRawEncode(){
        return getItem(ATTR_COLUMN_RAW_ENCODE);
    }
}
