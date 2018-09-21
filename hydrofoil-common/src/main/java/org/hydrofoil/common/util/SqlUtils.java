package org.hydrofoil.common.util;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.hydrofoil.common.graph.GraphProperty;
import org.hydrofoil.common.schema.ColumnSchema;
import org.hydrofoil.common.util.bean.BinaryArrayBlob;

import java.sql.Blob;
import java.sql.Clob;
import java.util.Objects;

/**
 * SqlUtils
 * <p>
 * package org.hydrofoil.common.util
 *
 * @author xie_yh
 * @date 2018/8/29 18:46
 */
public final class SqlUtils {

    /**
     * built-in raw data to accept data
     * @param columnSchema column ' schema
     * @param rawValue raw value,from database
     * @return graph data value
     * @throws Exception
     */
    public static Object rawDataToAcceptData(final ColumnSchema columnSchema,final Object rawValue) throws Exception{
        if(columnSchema == null){
            return Objects.toString(rawValue,null);
        }
        GraphProperty.PropertyType acceptType = columnSchema.getAcceptType();
        Object value = rawValue;
        if(rawValue instanceof String){
            value = EncodeUtils.charsetEncode((String) rawValue,columnSchema.getRawEncode());
        }
        if(value != null){
            if(acceptType == GraphProperty.PropertyType.Date &&
                    value instanceof String &&
                    StringUtils.isNotBlank(columnSchema.getRawFormat())){
                return EncodeUtils.parseDate((String) value,columnSchema.getRawFormat());
            }
            if(acceptType == GraphProperty.PropertyType.Blob){
                if(value instanceof Blob){
                    return new BinaryArrayBlob((Blob) value);
                }
                if(value instanceof byte[]){
                    return new BinaryArrayBlob((byte[]) value);
                }
            }
            if(acceptType == GraphProperty.PropertyType.String &&
                    value instanceof Clob){
                return parseClob((Clob) value);
            }
        }
        //call default handle
        return acceptType.apply(value);
    }

    public static String parseClob(Clob clob){
        try {
            return IOUtils.toString(clob.getCharacterStream());
        } catch (Throwable t) {
            t.printStackTrace();
        }
        return null;
    }

}
