package org.hydrofoil.common.schema;

import org.dom4j.Element;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.common.util.XmlUtils;

import java.util.Map;

/**
 * DataSourceSchema
 * data source config shcema
 * <p>
 * package org.hydrofoil.common.schema
 *
 * @author xie_yh
 * @date 2018/6/29 13:37
 */
public final class DataSourceSchema extends SchemaItem{

    private static final String ATTR_DATASOURCE_NAME = "name";
    private static final String ATTR_DATASOURCE_PROVIDER = "provider";

    private static final String ELEMENT_DATASOURCE_CONFIGITEM = "configitem";

    public DataSourceSchema(){}

    @Override
    void parse(final Element element){
        //base info
        String name = XmlUtils.attributeStringValue(element,ATTR_DATASOURCE_NAME);
        String provider = XmlUtils.attributeStringValue(element,ATTR_DATASOURCE_PROVIDER);

        ParameterUtils.notBlank(name);
        ParameterUtils.notBlank(provider);

        putItem(ATTR_DATASOURCE_NAME,name);
        putItem(ATTR_DATASOURCE_PROVIDER,provider);

        //config info
        putItem(ELEMENT_DATASOURCE_CONFIGITEM,XmlUtils.toStringMap(element));
    }

    /**
     * get datasource name
     * @return name
     */
    public String getDatasourceName(){
        return getItem(ATTR_DATASOURCE_NAME);
    }

    /**
     * get datasource provider full name
     * @return provider name
     */
    public String getProvider(){
        return getItem(ATTR_DATASOURCE_PROVIDER);
    }

    /**
     * get all config item
     * @return config item map
     */
    public Map<String,String> getConfigItems(){
        return getItemMap(ELEMENT_DATASOURCE_CONFIGITEM);
    }
}
