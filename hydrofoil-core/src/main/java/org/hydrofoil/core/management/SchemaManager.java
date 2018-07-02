package org.hydrofoil.core.management;

import org.hydrofoil.common.schema.DataSourceSchema;
import org.hydrofoil.common.schema.TableSchema;

import java.util.HashMap;
import java.util.Map;

/**
 * SchemaManager
 * <p>
 * package org.hydrofoil.core.management
 *
 * @author xie_yh
 * @date 2018/6/30 11:27
 */
public final class SchemaManager {

    /**
     * data source schema map
     */
    private Map<String,DataSourceSchema> dataSourceSchemaMap = new HashMap<>();

    /**
     * table schema map
     */
    private Map<String,TableSchema> tableSchemaMap = new HashMap<>();

    SchemaManager(){}


}
