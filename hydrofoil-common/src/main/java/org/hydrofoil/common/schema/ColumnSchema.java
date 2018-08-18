package org.hydrofoil.common.schema;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
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

    /**
     * index type define
     */
    public static final String COLUMN_INDEX_TYPE_NORMAL = "normal";
    public static final String COLUMN_INDEX_TYPE_TEXT = "text";

    public ColumnSchema(){}

    @Override
    void parse(final Element element){
        String name = XmlUtils.attributeStringValue(element,ATTR_COLUMN_NAME);
        String indexType = XmlUtils.attributeStringValue(element,ATTR_COLUMN_INDEX_TYPE,"");

        ParameterUtils.notBlank(name);
        ParameterUtils.checkValueNullable(indexType,COLUMN_INDEX_TYPE_NORMAL,
                COLUMN_INDEX_TYPE_TEXT);

        putItem(ATTR_COLUMN_NAME,name);
        putItem(ATTR_COLUMN_INDEX_TYPE,indexType);
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
}
