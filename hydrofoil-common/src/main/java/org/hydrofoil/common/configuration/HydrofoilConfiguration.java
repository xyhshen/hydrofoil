package org.hydrofoil.common.configuration;

import org.hydrofoil.common.util.DataUtils;

import java.io.InputStream;
import java.util.HashMap;
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

    private Map<String,Object> schemaFileMap;

    public HydrofoilConfiguration(){
        this.schemaFileMap = new HashMap<>();
    }

    public HydrofoilConfiguration putSchemaFile(String name,Object raw){
        schemaFileMap.put(name,raw);
        return this;
    }

    public InputStream getSchemaFile(String name){
        Object raw = schemaFileMap.get(name);
        if(raw == null){
            return null;
        }
        if(raw instanceof String){
            return DataUtils.openFile(Objects.toString(raw,null));
        }

        if(raw instanceof InputStream){
            return (InputStream) raw;
        }
        return null;
    }

}
