package org.hydrofoil.common.schema;

import org.apache.commons.collections4.MapUtils;
import org.dom4j.Element;
import org.hydrofoil.common.util.XmlUtils;

import java.io.InputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

/**
 * SchemaItem
 * schema base item
 * package org.hydrofoil.common.schema
 *
 * @author xie_yh
 * @date 2018/6/29 13:47
 */
@SuppressWarnings("unchecked")
public class SchemaItem {

    /**
     * schema map
     */
    private Map<String,Object> schemaMap = new HashMap<>();

    SchemaItem(){}

    public SchemaItem(Map<String,Object> schemaMap){
        this.schemaMap = schemaMap;
    }

    /**
     * read xml stream
     * @param is stream
     * @throws Exception
     */
    public void read(final InputStream is) throws Exception{
        parse(XmlUtils.getRoot(is));
    }

    /**
     * default read
     * @param element xml node
     */
    public void read(final Element element){
        parse(element);
    }

    /**
     * default parse
     * @param element xml root
     */
    void parse(final Element element){
        //empty
    }

    /**
     * put a list
     * @param name item name
     * @param object save object
     * @return this
     */
    public SchemaItem putItem(final String name,final Collection<String> object){
        schemaMap.put(name,object);
        return this;
    }

    /**
     * put a schema list
     * @param name schema name
     * @param object schema list
     * @return this
     */
    public <C extends SchemaItem> SchemaItem putSchemaItem(final String name,final Collection<C> object){
        schemaMap.put(name,object);
        return this;
    }

    /**
     * put a value
     * @param name item name
     * @param value item value
     * @return this
     */
    public SchemaItem putItem(final String name,final String value){
        schemaMap.put(name,value);
        return this;
    }

    /**
     * put a schema
     * @param name name
     * @param item item
     * @return this
     */
    public SchemaItem putSchemaItem(final String name,final SchemaItem item){
        schemaMap.put(name,item);
        return this;
    }

    /**
     * put a map
     * @param name item name
     * @param object map
     * @return this
     */
    public SchemaItem putItem(final String name,final Map<String,String> object){
        schemaMap.put(name,object);
        return this;
    }

    /**
     * put a schema map
     * @param name name
     * @param item item
     * @return this
     */
    public <C extends SchemaItem> SchemaItem putSchemaItem(final String name,final Map<String,C> item){
        schemaMap.put(name,item);
        return this;
    }

    /**
     * get value list
     * @param name item name
     * @return list
     */
    public Collection<String> getItemList(final String name){
        Object o = schemaMap.get(name);
        return o != null?(Collection<String>)o:null;
    }

    /**
     * get schema list
     * @param name schema item name
     * @return schema list
     */
    public <C extends SchemaItem> Collection<C> getSchemaList(final String name){
        Object o = schemaMap.get(name);
        return o != null?(Collection<C>)o:null;
    }

    /**
     * get value
     * @param name item name
     * @return value
     */
    public String getItem(final String name){
        return MapUtils.getString(schemaMap,name,null);
    }

    /**
     * get schema
     * @param name schema name
     * @return schema
     */
    public SchemaItem getSchemaItem(final String name){
        Object o = schemaMap.get(name);
        return o != null?(SchemaItem)o:null;
    }

    /**
     * get item map
     * @param name item name
     * @return item map
     */
    public Map<String,String> getItemMap(final String name){
        Object o = schemaMap.get(name);
        return o != null?(Map<String,String>)o:null;
    }

    /**
     * get schema map
     * @param name schema name
     * @return schema map
     */
    public <C extends SchemaItem> Map<String,C> getSchemaMap(final String name){
        Object o = schemaMap.get(name);
        return o != null?(Map<String,C>)o:null;
    }
}
