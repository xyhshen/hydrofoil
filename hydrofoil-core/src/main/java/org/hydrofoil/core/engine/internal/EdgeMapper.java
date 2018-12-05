package org.hydrofoil.core.engine.internal;

import org.apache.commons.collections4.MapUtils;
import org.hydrofoil.common.graph.*;
import org.hydrofoil.common.provider.datasource.RowStore;
import org.hydrofoil.common.schema.BaseElementSchema;
import org.hydrofoil.common.schema.EdgeSchema;
import org.hydrofoil.core.engine.EngineEdge;
import org.hydrofoil.core.engine.EngineVertex;
import org.hydrofoil.core.engine.management.SchemaManager;

import java.util.Map;
import java.util.Set;

/**
 * EdgeMapper
 * <p>
 * package org.hydrofoil.core.engine.internal
 *
 * @author xie_yh
 * @date 2018/7/15 18:47
 */
public class EdgeMapper extends AbstractElementMapper {
    public EdgeMapper(SchemaManager schemaManager) {
        super(schemaManager);
    }

    @Override
    protected BaseElementSchema getElementSchema(String label) {
        return schemaManager.getEdgeSchema(label);
    }

    @Override
    protected boolean checkElementIdType(GraphElementId elementId) {
        return elementId instanceof GraphEdgeId;
    }

    @Override
    protected BaseElementSchema getSchema(String label) {
        return schemaManager.getEdgeSchema(label);
    }

    @SuppressWarnings("unchecked")
    public ElementMapping toMapping(String label,Set<QMatch.Q> propertyQuerySet, EngineVertex vertex, EdgeDirection direction, Long start, Long limit) {
        final EdgeSchema edgeSchema = schemaManager.getEdgeSchema(label);
        final PropertyQueryCondition propertyQueryCondtion = createPropertyQueryCondtion(propertyQuerySet, edgeSchema);
        if(propertyQueryCondtion == null){
            return null;
        }
        if(vertex != null){
            Map<String,String> fields;
            if(direction == EdgeDirection.In){
                fields = edgeSchema.getTargetField();
            }else{
                fields = edgeSchema.getSourceField();
            }
            fields.forEach((vertexPropertyLabel,tableFieldname)->{
                Object value = MapUtils.getObject(vertex.elementId().unique(),vertexPropertyLabel);
                if(null == value){
                    return;
                }
                propertyQueryCondtion.getMainCondition().add(QMatch.eq(tableFieldname,value));
            });
        }

        return super.createScanMapping(propertyQueryCondtion,edgeSchema,start,limit);
    }

    GraphVertexId[] getEdgeVertexId(EdgeSchema edgeSchema,RowStore rowStore){
        GraphVertexId[] vertexIds = new GraphVertexId[2];
        String tableName = edgeSchema.getTable();
        GraphElementId.GraphElementBuilder fromBuilder = GraphElementId.builder(edgeSchema.getSourceLabel());
        edgeSchema.getSourceField().forEach((k,v)->{
            fromBuilder.unique(k,rowStore.value(tableName,v));
        });
        GraphElementId.GraphElementBuilder toBuilder = GraphElementId.builder(edgeSchema.getTargetLabel());
        edgeSchema.getTargetField().forEach((k,v)->{
            toBuilder.unique(k,rowStore.value(tableName,v));
        });
        vertexIds[0] = fromBuilder.buildVertexId();
        vertexIds[1] = toBuilder.buildVertexId();
        return vertexIds;
    }

    @SuppressWarnings("unchecked")
    public EngineEdge rowStoreToEdge(EdgeSchema edgeSchema, RowStore rowStore){
        //make edge id
        GraphVertexId[] vertexIds = getEdgeVertexId(edgeSchema,rowStore);
        //create edge
        return new EngineEdge(rowElementToId(edgeSchema,rowStore,GraphEdgeId.class),
                vertexIds[0],vertexIds[1],rowToProperties(edgeSchema, rowStore));
    }
}
