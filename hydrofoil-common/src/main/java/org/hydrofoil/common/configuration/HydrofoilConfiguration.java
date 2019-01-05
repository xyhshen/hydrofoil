package org.hydrofoil.common.configuration;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.common.util.ArgumentUtils;

import java.io.InputStream;
import java.util.Map;
import java.util.Objects;

/**
 * HydrofoilConfiguration
 * <p>
 * package org.hydrofoil.common.configuration
 *
 * @author xie_yh
 * @date 2018/7/3 17:34
 */
public class HydrofoilConfiguration {

    /**
     * prefix name
     */
    public static final String CONFIG_PREFIX = "hydrofoil";

    private Map<String,Object> configProperties;

    public HydrofoilConfiguration(){
        this.configProperties = DataUtils.newHashMapWithExpectedSize();
    }

    public HydrofoilConfiguration put(String name,Object content){
        configProperties.put(name,content);
        return this;
    }

    public Object getItem(HydrofoilConfigurationItem item){
        Object value = configProperties.get(item.getFullname());
        if(value == null){
            value = item.getDefaultValue();
        }
        if(value instanceof String &&
                StringUtils.isBlank(Objects.toString(value,null))){
            value = item.getDefaultValue();
        }
        return value;
    }

    public Long getLong(HydrofoilConfigurationItem item){
        return NumberUtils.toLong(Objects.toString(getItem(item),null));
    }

    public Integer getInt(HydrofoilConfigurationItem item){
        return NumberUtils.toInt(Objects.toString(getItem(item),null));
    }

    /**
     * get item
     * @param item
     * @return
     */
    public InputStream getStream(HydrofoilConfigurationItem item){
        ArgumentUtils.mustTrueMessage(item.getType() ==
                HydrofoilConfigurationItem.ConfigType.file,"must is file type");
        InputStream is = null;
        StreamType[] values = StreamType.values();
        for(StreamType streamType:values){
            Object o = MapUtils.getObject(configProperties,item.getFullname()
                    + "." + streamType.getName());
            if(o == null){
                continue;
            }
            is = streamType.getStream(o);
            if(is != null){
                break;
            }
        }
        return is;
    }

    public Map<String,Object> toMap(){
        return configProperties;
    }

}
