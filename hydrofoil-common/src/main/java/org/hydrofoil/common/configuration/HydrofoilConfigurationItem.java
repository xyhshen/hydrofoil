package org.hydrofoil.common.configuration;

/**
 * HydrofoilConfigurationItem
 * <p>
 * package org.hydrofoil.common.configuration
 *
 * @author xie_yh
 * @date 2018/7/3 17:28
 */
public enum  HydrofoilConfigurationItem {

    SchemaDatasource("schema.datasource","",false,ConfigType.file),
    SchemaDataset("schema.dataset","",false,ConfigType.file),
    SchemaMapper("schema.mapper","",false,ConfigType.file),
    QueryCacheTime("query.cache.time","30000",false,ConfigType.string),
    QueryCacheMaxsize("query.cache.maxsize","10000",false,ConfigType.string),
    TinkerpopDefaultReturnLength("tinkerpop.default.return.length","10000",false,ConfigType.string);

    /**
     * @return ConfigType
     * @see HydrofoilConfigurationItem#type
     **/
    public ConfigType getType() {
        return type;
    }

    enum ConfigType{
        string,
        file;
    };

    private final String name;

    private final String defaultValue;

    private final boolean required;

    private final ConfigType type;

    HydrofoilConfigurationItem(
            final String name,
            final String defaultValue,
            final boolean required,
            final ConfigType type
    ){
        this.name = name;
        this.defaultValue = defaultValue;
        this.required = required;
        this.type = type;
    }

    /**
     * @return String
     * @see HydrofoilConfigurationItem#name
     **/
    public String getName() {
        return name;
    }

    public String getFullname(){
        return HydrofoilConfiguration.CONFIG_PREFIX + "." + name;
    }

    /**
     * @return String
     * @see HydrofoilConfigurationItem#defaultValue
     **/
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * @return $field.TypeName
     * @see HydrofoilConfigurationItem#required
     **/
    public boolean isRequired() {
        return required;
    }
}
