package org.hydrofoil.core.standard.internal;

import org.apache.commons.lang3.StringUtils;
import org.hydrofoil.common.schema.PropertySchema;
import org.hydrofoil.common.schema.TableSchema;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.core.management.SchemaManager;

/**
 * GraphConditionUtils
 * <p>
 * package org.hydrofoil.core.standard.internal
 *
 * @author xie_yh
 * @date 2018/7/12 14:32
 */
final class MapperHelper {

    /**
     * get real table name
     * @param schemaManager schema manager
     * @param tableName tablename
     * @return real table name
     */
    static String getRealTableName(SchemaManager schemaManager,String tableName){
        TableSchema tableSchema = schemaManager.getTableSchema(tableName);
        ParameterUtils.notNull(tableSchema,"table schema");
        return tableSchema.getRealName();
    }

    static String getDatasourceName(SchemaManager schemaManager,String tableName){
        TableSchema tableSchema = schemaManager.getTableSchema(tableName);
        ParameterUtils.notNull(tableSchema,"table schema");
        return tableSchema.getDatasourceName();
    }

    static boolean isPropertyInMainTable(PropertySchema propertySchema){
        return StringUtils.isBlank(propertySchema.getTable());
    }

}
