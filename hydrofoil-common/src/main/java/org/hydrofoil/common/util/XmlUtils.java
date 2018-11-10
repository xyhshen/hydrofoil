package org.hydrofoil.common.util;

import org.apache.commons.collections4.ListUtils;
import org.apache.commons.lang3.StringUtils;
import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * XmlUtils
 * <p>
 * package org.hydrofoil.common.util
 *
 * @author xie_yh
 * @date 2018/6/30 9:31
 */
public final class XmlUtils {

    private XmlUtils(){}

    /**
     * get attribute value for string
     * @param element node
     * @param name attribute name
     * @return value
     */
    public static String attributeStringValue(final Element element,final String name){
        return attributeStringValue(element,name,null);
    }

    /**
     * get attribute value for string
     * @param element node
     * @param name attribute name
     * @param defaultValue default value
     * @return value
     */
    public static String attributeStringValue(final Element element,final String name,final String defaultValue){
        if(element == null){
            return defaultValue;
        }
        Attribute attribute = element.attribute(name);
        if(attribute == null){
            return defaultValue;
        }

        String value = attribute.getStringValue();
        return StringUtils.isNotBlank(value)?value:defaultValue;
    }

    /**
     * get xml root by stream
     * @param is input stream
     * @return root node
     */
    public static Element getRoot(final InputStream is) throws Exception {
        SAXReader reader = new SAXReader();
        Document document = reader.read(is);
        return document.getRootElement();
    }

    /**
     * list all children node
     * @param element parent node
     * @return node list
     */
    public static List<Element> listElement(final Element element){
        return listElement(element,null);
    }

    /**
     * list all children node
     * @param element parent node
     * @param name node name
     * @return node list
     */
    @SuppressWarnings("unchecked")
    public static List<Element> listElement(final Element element, final String name){
        List<Element> l;
        if(StringUtils.isNotBlank(name)){
            l = element.elements(name);
        }else{
            l = element.elements();
        }
        return l;
    }

    /**
     * element node to string map
     * @param element node
     * @return string map
     */
    @SuppressWarnings("unchecked")
    public static Map<String,String> toStringMap(final Element element){
        List elements = element.elements();
        Map<String,String> map = new TreeMap<>();
        elements.forEach((v)->{
            Element children = (Element) v;
            if(hasChildren(children)){
                return;
            }
            map.put(children.getName(),children.getText());
        });
        return map;
    }

    /**
     * is has children node
     * @param element node
     * @return result
     */
    public static boolean hasChildren(final Element element){
        return !ListUtils.emptyIfNull(listElement(element)).isEmpty();
    }
}
