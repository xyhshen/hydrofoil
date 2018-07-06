package org.hydrofoil.provider.mysql;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.BooleanUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.hydrofoil.common.util.ParameterUtils;

import java.util.Map;

/**
 * MysqlConfiguration
 * <p>
 * package org.hydrofoil.provider.mysql
 *
 * @author xie_yh
 * @date 2018/7/4 13:45
 */
public final class MysqlDatasourceSchema {

    /**
     * mysql jdbc
     */
    public final static String MYSQL_DRIVER_NAME = "com.mysql.jdbc.Driver";

    public enum DatasourceItem{

        /**
         * config item
         */
        ConnectUrl("url","",true),
        Username("username","",true),
        Password("password","",true),
        TestWhileIdle("testWhileIdle","false",false),
        ValidationQuery("validationQuery","",false),
        ValidationQueryTimeout("validationQueryTimeout","10",false),
        ConnectPoolMinIdle("connectPoolMinIdle","2",false),
        ConnectPoolMaxIdle("connectPoolMaxIdle","4",false),
        ConnectPoolMaxTotal("connectPoolMaxTotal","8",false),
        CacheTime("cacheTime","1000",false),
        CachePoolMaxSize("cachePoolMaxSize","1000",false);

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
                ParameterUtils.notBlank(value,name);
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
