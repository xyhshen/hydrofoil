package org.hydrofoil.common.graph;

import org.apache.commons.lang3.StringUtils;
import org.hydrofoil.common.util.bean.BinaryArrayBlob;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.common.util.ArgumentUtils;
import org.hydrofoil.common.util.bean.Beans;

import java.net.URL;
import java.sql.Blob;
import java.util.Date;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Stream;

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
    public enum PropertyType implements Function{

        /**
         * string
         */
        String("string",String.class,null) {
            @Override
            protected Object transform(Object o) {
                return Beans.toString(o);
            }
        },
        /**
         * byte char
         */
        Byte("byte",Byte.class,0) {
            @Override
            protected Object transform(Object o) {
                return Beans.toByte(o);
            }
        },
        /**
         * char
         */
        Char("char",Character.class,0) {
            @Override
            protected Object transform(Object o) {
                return Beans.toCharacter(o);
            }
        },
        /**
         * byte char
         */
        Boolean("boolean",Boolean.class,false) {
            @Override
            protected Object transform(Object o) {
                return Beans.toBoolean(o);
            }
        },
        /**
         * short 16bit
         */
        Short("short",Short.class,0) {
            @Override
            protected Object transform(Object o) {
                return Beans.toLong(o).shortValue();
            }
        },
        /**
         * int 32bit
         */
        Integer("integer",Integer.class,0) {
            @Override
            protected Object transform(Object o) {
                return Beans.toLong(o).intValue();
            }
        },
        /**
         * long 64bit
         */
        Long("long",Long.class,0L) {
            @Override
            protected Object transform(Object o) {
                return Beans.toLong(o);
            }
        },
        /**
         * float
         */
        Float("float",Float.class,0f) {
            @Override
            protected Object transform(Object o) {
                return Beans.toDouble(o).floatValue();
            }
        },
        /**
         * double
         */
        Double("double",Double.class,0d) {
            @Override
            protected Object transform(Object o) {
                return Beans.toDouble(o);
            }
        },
        /**
         * date
         */
        Date("date", Date.class,null) {
            @Override
            protected Object transform(Object o) {
                return Beans.toDate(o);
            }
        },
        /**
         * url
         */
        Url("url", URL.class,null) {
            @Override
            protected Object transform(Object o) {
                return Beans.toURL(o);
            }
        },
        /**
         * big data,blob
         */
        Blob("blob",BinaryArrayBlob.class,null) {
            @Override
            protected Object transform(Object o) {
                if(o instanceof Blob){
                    return o;
                }
                return null;
            }
        };

        /**
         * type java class
         */
        private final Class<?> valueClass;

        /**
         * name
         */
        private final String name;

        /**
         * null value
         */
        private final Object nullValue;

        PropertyType(final String name,final Class<?> valueClass,final Object nullValue){
            this.name = name;
            this.valueClass = valueClass;
            this.nullValue = nullValue;
        }

        public boolean isMatch(final Object value){
            return value != null && valueClass.isAssignableFrom(value.getClass());
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

        public static String[] names(){
            return Stream.of(PropertyType.values()).
                    map(PropertyType::typeName).
                    toArray(String[]::new);
        }

        public static PropertyType ofTypeName(String name){
            return (PropertyType) DataUtils.getOptional(Stream.of(PropertyType.values()).
                    filter(p->StringUtils.equalsIgnoreCase(name,p.typeName())).
                    findFirst());
        }

        public String typeName(){
            return name;
        }

        @Override
        public Object apply(Object o) {
            if(o == null){
                return nullValue;
            }
            if(o.getClass().equals(valueClass)){
                return o;
            }
            return transform(o);
        }

        protected abstract Object transform(Object o);
    }

    /**
     * property name
     */
    private final String name;

    /**
     * property content
     */
    private final Object content;

    public GraphProperty(final String name,final Object content){
        this.name = name;
        this.content = content;

        ArgumentUtils.mustTrue(PropertyType.checkValue(content),name + " checkValue");
    }

    /**
     * @return String
     * @see GraphProperty#name
     **/
    public String name() {
        return name;
    }

    /**
     * get property value content
     * @return raw value
     */
    public Object content(){
        return content;
    }

    @Override
    public String toString(){
        return Objects.toString(content, (String) PropertyType.String.nullValue);
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

    public Boolean toBoolean(){
        return PropertyType.Boolean.isMatch(content)?
                (Boolean) content:
                (Boolean) PropertyType.Boolean.nullValue;
    }

    public Character toChar(){
        return PropertyType.Char.isMatch(content)?
                (Character) content:
                (Character) PropertyType.Char.nullValue;
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

    public Blob toBlob(){
        return (Blob) (PropertyType.Blob.isMatch(content)?
                        content:
                        PropertyType.Blob.nullValue);
    }

    public URL toURL(){
        return PropertyType.Url.isMatch(content)?
                (URL) content:
                (URL) PropertyType.Url.nullValue;
    }

}
