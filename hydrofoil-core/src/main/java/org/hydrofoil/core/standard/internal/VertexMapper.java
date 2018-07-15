package org.hydrofoil.core.standard.internal;

import org.hydrofoil.common.graph.GraphElementId;
import org.hydrofoil.common.graph.GraphProperty;
import org.hydrofoil.common.graph.GraphVertexId;
import org.hydrofoil.common.provider.datasource.RowStore;
import org.hydrofoil.common.schema.AbstractElementSchema;
import org.hydrofoil.common.schema.VertexSchema;
import org.hydrofoil.core.management.SchemaManager;
import org.hydrofoil.core.standard.StandardProperty;
import org.hydrofoil.core.standard.StandardVertex;

import java.util.HashMap;
import java.util.Map;

/**
 * VertexMapper
 * <p>
 * package org.hydrofoil.core.standard.internal
 *
 * @author xie_yh
 * @date 2018/7/14 15:36
 */
public final class VertexMapper extends AbstractElementMapper{

    public VertexMapper(final SchemaManager schemaManager){
        super(schemaManager);
    }

    @Override
    protected AbstractElementSchema getElementSchema(String label) {
        return schemaManager.getVertexSchema(label);
    }

    @Override
    protected boolean checkElementIdType(GraphElementId elementId) {
        return elementId instanceof GraphVertexId;
    }

    public ElementMapping toMapping(GraphVertexId vertexId){
        return super.toMapping(vertexId,schemaManager.getVertexSchema(vertexId.label()));
    }

    public StandardVertex rowStoreToVertex(VertexSchema vertexSchema, RowStore rowStore){
        Map<String,StandardProperty> propertyMap = new HashMap<>();
        StandardVertex vertex = new StandardVertex(getVertexId(vertexSchema,rowStore),propertyMap);
        String tableName = MapperHelper.getRealTableName(schemaManager,vertexSchema.getTable());
        vertexSchema.getProperties().values().forEach((propertySchema) ->{
            if(MapperHelper.isPropertyInMainTable(propertySchema)){
                GraphProperty graphProperty =
                        new GraphProperty(propertySchema.getLabel(),
                                rowStore.value(tableName,propertySchema.getField()));
                propertyMap.put(propertySchema.getLabel(),
                        new StandardProperty(propertySchema.getLabel(),graphProperty,false,false));
            }
        });
        return vertex;
    }

    private  GraphVertexId getVertexId(VertexSchema vertexSchema,RowStore rowStore){
        GraphVertexId vertexId = new GraphVertexId(vertexSchema.getLabel());
        String tableName = MapperHelper.getRealTableName(schemaManager,vertexSchema.getTable());

        vertexSchema.getProperties().values().forEach((propertySchema) -> {
            if(propertySchema.isPrimary()){
                vertexId.unique(propertySchema.getLabel(),rowStore.value(tableName,propertySchema.getField()));
            }
        });
        return vertexId;
    }

}
