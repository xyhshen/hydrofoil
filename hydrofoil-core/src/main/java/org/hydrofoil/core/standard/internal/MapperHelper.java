package org.hydrofoil.core.standard.internal;

import org.apache.commons.lang3.StringUtils;
import org.hydrofoil.common.graph.QMatch;
import org.hydrofoil.common.schema.AbstractElementSchema;
import org.hydrofoil.common.schema.ColumnSchema;
import org.hydrofoil.common.schema.PropertySchema;
import org.hydrofoil.common.schema.TableSchema;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.core.standard.management.SchemaManager;

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

    static boolean checkQueriable(SchemaManager schemaManager,
                                 AbstractElementSchema elementSchema,
                                 QMatch.Q query){
        if(query.isNot()){
            //in any case,all not support
            return false;
        }
        PropertySchema propertySchema = elementSchema.
                getProperties().get(query.pair().name());
        ColumnSchema columnSchema = schemaManager.getColumnSchema(propertySchema.getTable(),
                propertySchema.getField());
        if(query.type() != QMatch.QType.like){
            if(propertySchema.isPrimary()){
                return true;
            }
            if(columnSchema == null || !columnSchema.isSupportedNormalIndex()){
                return false;
            }
        }else{
            if(columnSchema == null || !columnSchema.isSupportedTextIndex()){
                return false;
            }
        }
        return true;
    }

}
