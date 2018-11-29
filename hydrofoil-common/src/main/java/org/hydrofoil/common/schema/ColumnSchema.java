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

    /**
     * index type mask
     */
    public static final int INDEX_TYPE_MASK_NORMAL = 0x1;
    public static final int INDEX_TYPE_MASK_TEXT = 0x2;
    public static final int INDEX_TYPE_MASK_PRIMARY = 0x4;

    private static final List<BiConsumer<Element,SchemaItem>> DEFINES = Arrays.asList(
            SchemaItems.attributeDefine(ATTR_COLUMN_NAME,true),
            SchemaItems.attributeDefine(ATTR_COLUMN_INDEX_TYPE,false),
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

    private int indexType = 0;

    public ColumnSchema(){}

    @Override
    void parse(final Element node){
        //load schema
        loadSchema(node,DEFINES);

        String indexTypeText = getItem(ATTR_COLUMN_INDEX_TYPE);
        if(StringUtils.contains(indexTypeText,COLUMN_INDEX_TYPE_PRIMARY)){
            indexType |= INDEX_TYPE_MASK_PRIMARY;
        }
        if(StringUtils.contains(indexTypeText,COLUMN_INDEX_TYPE_NORMAL)){
            indexType |= INDEX_TYPE_MASK_NORMAL;
        }
        if(StringUtils.contains(indexTypeText,COLUMN_INDEX_TYPE_TEXT)){
            indexType |= INDEX_TYPE_MASK_TEXT;
        }
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
    public int getIndexType(){
        return indexType;
    }

    public boolean isSupportedNormalIndex(){
        return (indexType & INDEX_TYPE_MASK_NORMAL) != 0;
    }

    public boolean isSupportedTextIndex(){
        return (indexType & INDEX_TYPE_MASK_TEXT) != 0;
    }

    public boolean isSupportedPrimaryIndex(){
        return (indexType & INDEX_TYPE_MASK_PRIMARY) != 0;
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
