package org.hydrofoil.common.schema;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.hydrofoil.common.graph.GraphProperty;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;

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
    public static final String COLUMN_INDEX_TYPE_PRIMARY = "primary";

    private static final List<BiConsumer<Element,SchemaItem>> DEFINES = Arrays.asList(
            SchemaItems.attributeDefine(ATTR_COLUMN_NAME,true),
            SchemaItems.attributeDefine(ATTR_COLUMN_INDEX_TYPE,false,
                    p->StringUtils.equalsAnyIgnoreCase(Objects.toString(p),
                            COLUMN_INDEX_TYPE_NORMAL,COLUMN_INDEX_TYPE_TEXT,COLUMN_INDEX_TYPE_PRIMARY)),
            SchemaItems.attributeDefine(ATTR_COLUMN_ACCEPT_TYPE,false,
                    p->StringUtils.equalsAnyIgnoreCase(Objects.toString(p),
                            GraphProperty.PropertyType.names()),
                    GraphProperty.PropertyType.String.typeName()),
            SchemaItems.attributeDefine(ATTR_COLUMN_RAW_TYPE),
            SchemaItems.attributeDefine(ATTR_COLUMN_RAW_FORMAT),
            SchemaItems.attributeDefine(ATTR_COLUMN_RAW_ENCODE,false,null,"utf-8")
    );

    /**
     * column data accept type
     */
    private GraphProperty.PropertyType acceptType;

    public ColumnSchema(){}

    @Override
    void parse(final Element node){
        //load schema
        loadSchema(node,DEFINES);
        //set accept type
        this.acceptType = GraphProperty.PropertyType.ofTypeName(getItem(ATTR_COLUMN_ACCEPT_TYPE));
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

    public boolean isSupportedPrimaryIndex(){
        return StringUtils.equalsIgnoreCase(getIndexType(),
                COLUMN_INDEX_TYPE_PRIMARY);
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

    @Override
    public String getId(){
        return getColumnName();
    }
}
