package org.hydrofoil.common.schema;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.common.util.XmlUtils;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

/**
 * TableSchema
 * <p>
 * package org.hydrofoil.common.schema
 *
 * @author xie_yh
 * @date 2018/6/30 10:47
 */
public class TableSchema extends SchemaItem{

    private static final String ATTR_TABLE_NAME = "name";
    private static final String ATTR_TABLE_DATASOURCE = "datasource";
    private static final String ATTR_TABLE_PACKAGE = "package";
    private static final String ATTR_TABLE_REALNAME = "realname";

    private static final String NODE_TABLE_COLUMN = "column";
    private static final String NODE_TABLE_OPTION = "option";

    private static final String ATTR_TABLE_OPTION_NAME = "name";

    private static final List<BiConsumer<Element,SchemaItem>> DEFINES = Arrays.asList(
            SchemaItems.attributeDefine(ATTR_TABLE_NAME,true),
            SchemaItems.attributeDefine(ATTR_TABLE_DATASOURCE),
            SchemaItems.attributeDefine(ATTR_TABLE_REALNAME),
            SchemaItems.attributeDefine(ATTR_TABLE_PACKAGE),
            SchemaItems.nodeDefine(NODE_TABLE_COLUMN,ColumnSchema.class)
    );

    public TableSchema(){}

    /**
     * parse
     * @param node xml root
     */
    @Override
    void parse(final Element node){
        //base info
        loadSchema(node,DEFINES);

        //load option
        loadOptions(node);
    }

    private void loadColumns(final Element node){
        List<Element> elements = XmlUtils.listElement(node,
                NODE_TABLE_COLUMN);
        Map<String,ColumnSchema> map = DataUtils.newHashMapWithExpectedSize(elements.size());
        for(Element element:elements){
            ColumnSchema columnSchema = new ColumnSchema();
            columnSchema.parse(element);
            map.put(columnSchema.getColumnName(),columnSchema);
        }
        putSchemaItem(NODE_TABLE_COLUMN,map);
    }

    private void loadOptions(final Element node){
        List<Element> elements = XmlUtils.listElement(node,
                NODE_TABLE_OPTION);
        Map<String,String> map = DataUtils.newHashMapWithExpectedSize(elements.size());
        for(Element element:elements){
            String name = element.attributeValue(ATTR_TABLE_OPTION_NAME);
            ParameterUtils.notBlank(name);
            map.put(name,element.getStringValue());
        }
        putItem(NODE_TABLE_OPTION,map);
    }

    /**
     * get table name
     * @return table name
     */
    public String getName(){
        return getItem(ATTR_TABLE_NAME);
    }

    /**
     * get datasource name
     * @return datasource name
     */
    public String getDatasourceName(){
        return getItem(ATTR_TABLE_DATASOURCE);
    }

    /**
     * get table real name
     * @return table real name
     */
    public String getRealName(){
        return StringUtils.defaultString(getItem(ATTR_TABLE_REALNAME),getName());
    }

    /**
     * get table package,or path
     * @return package
     */
    public String getPackage(){
        return getItem(ATTR_TABLE_PACKAGE);
    }

    /**
     * get column define
     * @return column map
     */
    public Map<String,ColumnSchema> getColumns(){
        return getSchemaMap(NODE_TABLE_COLUMN);
    }

    /**
     * get option map
     * @return option map
     */
    public Map<String,String> getOptions(){
        return getItemMap(NODE_TABLE_OPTION);
    }

    @Override
    public String getId(){
        return getName();
    }

}
