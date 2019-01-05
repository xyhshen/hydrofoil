package org.hydrofoil.provider.jdbc;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.hydrofoil.common.util.ArgumentUtils;

import java.util.Map;

/**
 * MysqlConfiguration
 * <p>
 * package org.hydrofoil.provider.jdbc
 *
 * @author xie_yh
 * @date 2018/7/4 13:45
 */
public final class JdbcDatasourceSchema {

    /**
     * jdbc jdbc
     */
    public enum DataBaseType{

        //mysql
        Mysql("mysql","com.mysql.jdbc.Driver");

        private final String name;

        private final String className;

        DataBaseType(final String name,final String className){
            this.name = name;
            this.className = className;
        }

        /**
         * @return String
         * @see DataBaseType#name
         **/
        public String getName() {
            return name;
        }

        /**
         * @return String
         * @see DataBaseType#className
         **/
        public String getClassName() {
            return className;
        }

        public static DataBaseType of(final String name){
            final DataBaseType[] values = DataBaseType.values();
            for(DataBaseType dbType:values){
                if(StringUtils.equalsIgnoreCase(name,dbType.name)){
                    return dbType;
                }
            }
            return null;
        }
    }

    public enum DatasourceItem{

        /**
         * config item
         */
        DbType("dbType","",true),
        ConnectUrl("url","",true),
        Username("username","",true),
        Password("password","",true),
        TestWhileIdle("testWhileIdle","false",false),
        ConnectPoolMinIdle("connectPoolMinIdle","2",false),
        ConnectPoolMaxIdle("connectPoolMaxIdle","4",false),
        ConnectPoolMaxTotal("connectPoolMaxTotal","8",false);

        private final String name;
        private final String defaultValue;
        private final boolean required;

        DatasourceItem(final String name,final String defaultValue,final boolean required){
            this.name = name;
            this.defaultValue = defaultValue;
            this.required = required;
        }

        /**
         * get name
         * @return name
         */
        public String getName(){
            return name;
        }

        /**
         * get config value
         * @param configMap config map
         * @return value
         */
        public String toString(final Map<String,String> configMap){
            String value = MapUtils.getString(configMap, name);
            if(required){
                ArgumentUtils.notBlank(value,name);
            }
            return StringUtils.defaultString(value,defaultValue);
        }

        public boolean toBoolean(final Map<String,String> configMap){
            return BooleanUtils.toBoolean(toString(configMap));
        }

        public int toInteger(final Map<String,String> configMap){
            return NumberUtils.toInt(toString(configMap));
        }
    }

}
