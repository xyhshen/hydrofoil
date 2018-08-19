package org.hydrofoil.core.standard.internal;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.SetUtils;
import org.hydrofoil.common.graph.*;
import org.hydrofoil.common.provider.datasource.RowStore;
import org.hydrofoil.common.schema.AbstractElementSchema;
import org.hydrofoil.common.schema.EdgeSchema;
import org.hydrofoil.common.schema.PropertySchema;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.core.standard.StandardEdge;
import org.hydrofoil.core.standard.StandardProperty;
import org.hydrofoil.core.standard.StandardVertex;
import org.hydrofoil.core.standard.management.SchemaManager;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * EdgeMapper
 * <p>
 * package org.hydrofoil.core.standard.internal
 *
 * @author xie_yh
 * @date 2018/7/15 18:47
 */
public class EdgeMapper extends AbstractElementMapper {
    public EdgeMapper(SchemaManager schemaManager) {
        super(schemaManager);
    }

    @Override
    protected AbstractElementSchema getElementSchema(String label) {
        return schemaManager.getEdgeSchema(label);
    }

    @Override
    protected boolean checkElementIdType(GraphElementId elementId) {
        return elementId instanceof GraphEdgeId;
    }

    @Override
    protected AbstractElementSchema getSchema(String label) {
        return schemaManager.getEdgeSchema(label);
    }

    public ElementMapping toMapping(GraphEdgeId edgeId){
        EdgeSchema edgeSchema = schemaManager.getEdgeSchema(edgeId.label());
        Set<QMatch.Q> mainCondition = new HashSet<>();
        edgeSchema.getProperties().forEach((k,v)->{
            if(v.isPrimary()){
                Object value = MapUtils.getObject(edgeId.unique(),v.getLabel());
                mainCondition.add(QMatch.eq(v.getField(),value));
            }
        });
        return super.toMapping(mainCondition,edgeSchema,0L,1L);
    }

    @SuppressWarnings("unchecked")
    public ElementMapping toMapping(Set<QMatch.Q> propertyQuerySet,StandardVertex vertex,EdgeSchema edgeSchema, EdgeDirection direction,Long start,Long limit) {
        Set<QMatch.Q> mainCondition = new HashSet<>();
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
                mainCondition.add(QMatch.eq(tableFieldname,value));
            });
        }
        mainCondition.addAll(SetUtils.emptyIfNull(propertyQuerySet).stream().
                filter(propertyQuery->MapperHelper.checkQueriable(schemaManager,edgeSchema,propertyQuery)).
                map((propertyQuery)->{
            PropertySchema propertySchema = edgeSchema.getProperties().get(propertyQuery.pair().name());
            if(propertySchema == null){
                return null;
            }
            QMatch.Q clone = propertyQuery.clone();
            clone.pair().name(propertySchema.getField());
            return clone;
        }).collect(Collectors.toSet()));

        return super.toMapping(mainCondition,edgeSchema,start,limit);
    }

    GraphVertexId[] getEdgeVertexId(EdgeSchema edgeSchema,RowStore rowStore){
        GraphVertexId[] vertexIds = new GraphVertexId[2];
        String tableName = MapperHelper.getRealTableName(schemaManager,edgeSchema.getTable());
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

    public StandardEdge rowStoreToEdge(EdgeSchema edgeSchema, RowStore rowStore){
        Map<String,StandardProperty> propertyMap = DataUtils.newMapWithMaxSize(edgeSchema.getProperties().size());
        GraphVertexId[] vertexIds = getEdgeVertexId(edgeSchema,rowStore);
        StandardEdge edge = new StandardEdge(rowElementToId(edgeSchema,rowStore,GraphEdgeId.class),
                vertexIds[0],vertexIds[1],propertyMap);
        String tableName = MapperHelper.getRealTableName(schemaManager,edgeSchema.getTable());
        edgeSchema.getProperties().values().forEach((propertySchema) ->{
            if(MapperHelper.isPropertyInMainTable(propertySchema)){
                GraphProperty graphProperty =
                        new GraphProperty(propertySchema.getLabel(),
                                rowStore.value(tableName,propertySchema.getField()));
                propertyMap.put(propertySchema.getLabel(),
                        new StandardProperty(propertySchema.getLabel(),graphProperty,false));
            }
        });
        return edge;
    }
}
