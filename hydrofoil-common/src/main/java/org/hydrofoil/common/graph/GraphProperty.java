package org.hydrofoil.common.graph;

import org.hydrofoil.common.util.ParameterUtils;

import java.net.URL;
import java.util.Date;

/**
 * GraphProperty
 * <p>
 * package org.hydrofoil.common.graph
 *
 * @author xie_yh
 * @date 2018/7/11 16:44
 */
public final class GraphProperty {

    /**
     * property atom type
     */
    public enum PropertyType{

        String("string",String.class,null),
        Byte("short",Byte.class,null),
        Short("short",Short.class,null),
        Integer("integer",Integer.class,null),
        Long("long",Long.class,null),
        Float("float",Float.class,null),
        Double("double",Double.class,null),
        Date("date", Date.class,null),
        Url("url", URL.class,null),
        Blob("blob",byte[].class,null);

        /**
         * type java class
         */
        private final Class<?> clz;

        /**
         * name
         */
        private final String name;

        /**
         * null value
         */
        private final Object nullValue;

        PropertyType(final String name,final Class<?> clz,final Object nullValue){
            this.name = name;
            this.clz = clz;
            this.nullValue = nullValue;
        }

        public boolean isMatch(final Object value){
            return value != null && clz.isAssignableFrom(value.getClass());
        }

        public static boolean checkValue(final Object value){
            if(value == null){
                return true;
            }
            for (PropertyType propertyType : PropertyType.values()) {
                if(propertyType.isMatch(value)){
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * property label
     */
    private final String label;

    /**
     * property content
     */
    private final Object content;

    public GraphProperty(final String label,final Object content){
        this.label = label;
        this.content = content;

        ParameterUtils.mustTrue(PropertyType.checkValue(content),label + " checkValue");
    }

    /**
     * @return String
     * @see GraphProperty#label
     **/
    public String getLabel() {
        return label;
    }

    @Override
    public String toString(){
        return PropertyType.String.isMatch(content)?
                (String)content:
                (String) PropertyType.String.nullValue;
    }

    public Integer toInteger(){
        return PropertyType.Integer.isMatch(content)?
                (Integer) content:
                (Integer) PropertyType.Integer.nullValue;
    }

    public Long toLong(){
        return PropertyType.Long.isMatch(content)?
                (Long) content:
                (Long) PropertyType.Long.nullValue;
    }

    public Short toShort(){
        return PropertyType.Short.isMatch(content)?
                (Short) content:
                (Short) PropertyType.Short.nullValue;
    }

    public Byte toByte(){
        return PropertyType.Byte.isMatch(content)?
                (Byte) content:
                (Byte) PropertyType.Byte.nullValue;
    }

    public Date toDate(){
        return PropertyType.Date.isMatch(content)?
                (Date) content:
                (Date) PropertyType.Date.nullValue;
    }

    public Float toFloat(){
        return PropertyType.Float.isMatch(content)?
                (Float) content:
                (Float) PropertyType.Float.nullValue;
    }

    public Double toDouble(){
        return PropertyType.Double.isMatch(content)?
                (Double) content:
                (Double) PropertyType.Double.nullValue;
    }

    public byte[] toBlob(){
        return PropertyType.Blob.isMatch(content)?
                (byte[]) content:
                (byte[]) PropertyType.Blob.nullValue;
    }

    public URL toURL(){
        return PropertyType.Url.isMatch(content)?
                (URL) content:
                (URL) PropertyType.Url.nullValue;
    }

}
