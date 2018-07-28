package org.hydrofoil.core.standard.internal;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.SetUtils;
import org.hydrofoil.common.graph.GraphElementId;
import org.hydrofoil.common.graph.GraphProperty;
import org.hydrofoil.common.graph.GraphVertexId;
import org.hydrofoil.common.graph.QMatch;
import org.hydrofoil.common.provider.datasource.RowStore;
import org.hydrofoil.common.schema.AbstractElementSchema;
import org.hydrofoil.common.schema.PropertySchema;
import org.hydrofoil.common.schema.VertexSchema;
import org.hydrofoil.core.standard.management.SchemaManager;
import org.hydrofoil.core.standard.StandardProperty;
import org.hydrofoil.core.standard.StandardVertex;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

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
        VertexSchema vertexSchema = schemaManager.getVertexSchema(vertexId.label());
        Set<QMatch.Q> mainCondition = new HashSet<>();
        vertexSchema.getProperties().forEach((k,v)->{
            if(v.isPrimary()){
                Object value = MapUtils.getObject(vertexId.unique(),v.getLabel());
                mainCondition.add(QMatch.eq(v.getField(),value));
            }
        });

        return super.toMapping(mainCondition,vertexSchema,0L,1L);
    }

    public ElementMapping toMapping(String label, Set<QMatch.Q> propertyQuerySet,Long start,Long limit){
        VertexSchema vertexSchema = schemaManager.getVertexSchema(label);
        Set<QMatch.Q> mainCondition = SetUtils.emptyIfNull(propertyQuerySet).stream().map((propertyQuery)->{
            PropertySchema propertySchema = vertexSchema.getProperties().get(propertyQuery.pair().name());
            QMatch.Q clone = propertyQuery.clone();
            clone.pair().name(propertySchema.getField());
            return clone;
        }).collect(Collectors.toSet());
        return toMapping(mainCondition,vertexSchema,start,limit);
    }

    public StandardVertex rowStoreToVertex(VertexSchema vertexSchema, RowStore rowStore){
        Map<String,StandardProperty> propertyMap = new HashMap<>();
        StandardVertex vertex = new StandardVertex(rowElementToId(vertexSchema,rowStore,GraphVertexId.class),propertyMap);
        String tableName = MapperHelper.getRealTableName(schemaManager,vertexSchema.getTable());
        vertexSchema.getProperties().values().forEach((propertySchema) ->{
            if(MapperHelper.isPropertyInMainTable(propertySchema)){
                GraphProperty graphProperty =
                        new GraphProperty(propertySchema.getLabel(),
                                rowStore.value(tableName,propertySchema.getField()));
                propertyMap.put(propertySchema.getLabel(),
                        new StandardProperty(propertySchema.getLabel(),graphProperty,false));
            }
        });
        return vertex;
    }

}
