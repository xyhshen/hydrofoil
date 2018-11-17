package org.hydrofoil.core.engine.internal;

import org.apache.commons.lang3.StringUtils;
import org.hydrofoil.common.graph.QMatch;
import org.hydrofoil.common.provider.datasource.BaseRowQuery;
import org.hydrofoil.common.provider.datasource.RowStore;
import org.hydrofoil.common.schema.*;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.core.engine.management.SchemaManager;

import java.util.Map;

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
        ParameterUtils.notNull(tableSchema,"table schema");
        String datasourceName =  tableSchema.getDatasourceName();
        if(!StringUtils.isBlank(datasourceName)){
            return datasourceName;
        }
        //by namespace get datasource
        ParameterUtils.notBlank(tableSchema.getNamespace());
        NamespaceSchema namespaceSchema = schemaManager().getNamespaceSchema(tableSchema.getNamespace());
        ParameterUtils.notNull(namespaceSchema);
        return namespaceSchema.getDatasourceName();
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
            if(columnSchema == null || !columnSchema.isSupportedNormalIndex()){
                return false;
            }
        }else{
            if(columnSchema == null ||
                    !(columnSchema.isSupportedTextIndex() ||
                    columnSchema.isSupportedNormalIndex())){
                return false;
            }
        }
        return true;
    }

    default Object getPropertyValue(BaseElementSchema elementSchema, PropertySchema propertySchema, RowStore rowStore){
        Object value;
        if(isPropertyInMainTable(propertySchema)){
            value = rowStore.value(elementSchema.getTable(),propertySchema.getField());
        }else{
            final Map<String, LinkSchema> linkSchemaMap = elementSchema.getLinks();
            final LinkSchema linkSchema = linkSchemaMap.get(propertySchema.getLinkTable());
            value = rowStore.value(linkSchema.getTable(),propertySchema.getField());
        }

        return value;
    }

    default ElementMapping createMappingElement(BaseRowQuery rowQuery,BaseElementSchema elementSchema){
        ElementMapping elementMapping = new ElementMapping();
        elementMapping.setSchemaItem(elementSchema);
        elementMapping.setQueryRequest(rowQuery);
        elementMapping.setDatasource(getDatasourceName(elementSchema.getTable()));
        return elementMapping;
    }

}
