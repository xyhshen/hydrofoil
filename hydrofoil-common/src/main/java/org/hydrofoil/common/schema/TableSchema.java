package org.hydrofoil.common.schema;

import org.apache.commons.lang3.StringUtils;
import org.dom4j.Element;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.common.util.XmlUtils;

import java.util.List;
import java.util.Map;

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
    private static final String ATTR_TABLE_REALNAME = "realname";

    private static final String NODE_TABLE_COLUMN = "column";

    public TableSchema(){}

    /**
     * parse
     * @param element xml root
     */
    @Override
    void parse(final Element element){
        //base info
        String name = XmlUtils.attributeStringValue(element,ATTR_TABLE_NAME);
        String datasource = XmlUtils.attributeStringValue(element,ATTR_TABLE_DATASOURCE);
        String realname = XmlUtils.attributeStringValue(element,ATTR_TABLE_REALNAME);

        ParameterUtils.notBlank(name);
        ParameterUtils.notBlank(datasource);

        putItem(ATTR_TABLE_NAME,name);
        putItem(ATTR_TABLE_DATASOURCE,datasource);
        putItem(ATTR_TABLE_REALNAME,realname);

        loadColumns(element);
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
        String realName = getItem(ATTR_TABLE_REALNAME);
       return StringUtils.isNotBlank(realName)?realName:getName();
    }

    public Map<String,ColumnSchema> getColumns(){
        return getSchemaMap(NODE_TABLE_COLUMN);
    }

}
