package org.hydrofoil.core.engine.internal;

import org.apache.commons.lang3.StringUtils;
import org.hydrofoil.common.graph.QMatch;
import org.hydrofoil.common.provider.datasource.BaseRowQuery;
import org.hydrofoil.common.provider.datasource.RowStore;
import org.hydrofoil.common.schema.*;
import org.hydrofoil.common.util.ArgumentUtils;
import org.hydrofoil.core.engine.management.SchemaManager;

/**
 * GraphConditionUtils
 * <p>
 * package org.hydrofoil.core.engine.internal
 *
 * @author xie_yh
 * @date 2018/7/12 14:32
 */
interface MapperHelper {

    SchemaManager schemaManager();

    default String getDatasourceName(String tableName){
        TableSchema tableSchema = schemaManager().getTableSchema(tableName);
        ArgumentUtils.notNull(tableSchema,"table schema");
        String datasourceName =  tableSchema.getDatasourceName();
        if(!StringUtils.isBlank(datasourceName)){
            return datasourceName;
        }
        //by package get datasource
        ArgumentUtils.notBlank(tableSchema.getPackage());
        PackageSchema packageSchema = schemaManager().getPackageSchema(tableSchema.getPackage());
        ArgumentUtils.notNull(packageSchema);
        return packageSchema.getDatasourceName();
    }

    /**
     * judgement is main table property
     * @param propertySchema property is in main table
     * @return result
     */
    default boolean isPropertyInMainTable(PropertySchema propertySchema){
        return StringUtils.isBlank(propertySchema.getLinkTable());
    }

    /**
     * judgement property of element support index query
     * @param elementSchema element,vertex or edge
     * @param query query field
     * @return result
     */
    default boolean checkQueriable(BaseElementSchema elementSchema,
                                 QMatch.Q query){
        PropertySchema propertySchema = elementSchema.
                getProperties().get(query.pair().name());
        String tableName = elementSchema.getTable();
        if(!isPropertyInMainTable(propertySchema)){
            tableName = propertySchema.getLinkTable();
        }
        ColumnSchema columnSchema = schemaManager().getColumnSchema(tableName,
                propertySchema.getField());
        if(query.type() != QMatch.QType.like){
            if(propertySchema.isPrimary()){
                return true;
            }
            if(columnSchema == null || !columnSchema.canNormalQuery()){
                return false;
            }
        }else{
            if(columnSchema == null ||
                    !columnSchema.canFullTextQuery()){
                return false;
            }
        }
        return true;
    }

    default Object getPropertyValue(BaseElementSchema elementSchema, LinkSchema linkSchema,PropertySchema parentSchema,PropertySchema propertySchema, RowStore rowStore){
        String tableName;
        String fieleName;
        if(parentSchema == null){
            tableName = linkSchema!=null?linkSchema.getTable():elementSchema.getTable();
            fieleName = propertySchema.getField();
        }else{
            tableName = linkSchema.getTable();
            fieleName = propertySchema.getField();
        }
        if(rowStore.isSmallRow()){
            return rowStore.value(null,propertySchema.getField());
        }
        else{
            return rowStore.value(tableName,fieleName);
        }
    }

    default ElementMapping createMappingElement(BaseRowQuery rowQuery,BaseElementSchema elementSchema){
        ElementMapping elementMapping = new ElementMapping();
        elementMapping.setSchemaItem(elementSchema);
        elementMapping.setQueryRequest(rowQuery);
        elementMapping.setDatasource(getDatasourceName(elementSchema.getTable()));
        return elementMapping;
    }

}
