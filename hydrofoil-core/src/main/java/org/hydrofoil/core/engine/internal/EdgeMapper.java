package org.hydrofoil.core.engine.internal;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.MultiMapUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.hydrofoil.common.graph.*;
import org.hydrofoil.common.provider.datasource.BaseRowQuery;
import org.hydrofoil.common.provider.datasource.RowStore;
import org.hydrofoil.common.schema.BaseElementSchema;
import org.hydrofoil.common.schema.EdgeSchema;
import org.hydrofoil.common.schema.VertexSchema;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.common.util.collect.FixedArrayMap;
import org.hydrofoil.core.engine.EngineEdge;
import org.hydrofoil.core.engine.EngineVertex;
import org.hydrofoil.core.engine.management.SchemaManager;

import java.util.*;
import java.util.stream.Collectors;

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

    private boolean useOtherContactEdgeProperty(final EdgeSchema edgeSchema,final boolean sourceVertex){
        String label;
        Map<String,String> fieldMap;
        if(sourceVertex){
            fieldMap = edgeSchema.getSourceField();
            label = edgeSchema.getSourceLabel();
        }else{
            fieldMap = edgeSchema.getTargetField();
            label = edgeSchema.getSourceLabel();
        }

        final VertexSchema vertexSchema = schemaManager().getVertexSchema(label);
        if(CollectionUtils.isEqualCollection(
                vertexSchema.getPrimaryKeys(),
                fieldMap.keySet()
        )){
            return false;
        }
        return true;
    }



    @SuppressWarnings("unchecked")
    public ElementMapping toMapping(final String label,final Set<QMatch.Q> propertyQuerySet, final Collection<EngineVertex> vertices, final EdgeDirection direction, final Long start, final Long limit) {
        final EdgeSchema edgeSchema = schemaManager.getEdgeSchema(label);
        final PropertyQueryCondition propertyQueryCondtion = createPropertyQueryCondtion(propertyQuerySet, edgeSchema);
        if(propertyQueryCondtion == null){
            return null;
        }
        if(CollectionUtils.isNotEmpty(vertices)){
            //if not empty
            //process vertex
            Map<String,String> fields;
            //is use primary key
            boolean useOtherProperty = useOtherContactEdgeProperty(edgeSchema,direction != EdgeDirection.In);
            //get edge and vertex property
            if(direction == EdgeDirection.In){
                fields = edgeSchema.getTargetField();
            }else{
                fields = edgeSchema.getSourceField();
            }
            //property to field
            final Map<String, Pair<String,String>> property2Field = getPropertyToFieldMapOfElement(edgeSchema);
            Map<String,Integer> keyMap = FixedArrayMap.keyMapOf(property2Field.values().
                    stream().map(Pair::getValue).collect(Collectors.toList()));
            String linkName = null;
            if(property2Field.size() == 1){
                final Pair<String, String> pair = DataUtils.mapFirst(property2Field);
                linkName = pair.getLeft();
            }

            //every vertex get out property convert to edge field
            List<Map<String,Object>> valueMaps = new ArrayList<>(vertices.size());
            for(EngineVertex vertex:vertices) {
                Map<String,Object> valueMap = new FixedArrayMap<>(keyMap);
                fields.forEach((vertexPropertyLabel,edgePropertyLabel)->{
                    Object value;
                    if (useOtherProperty) {
                        value = vertex.property(vertexPropertyLabel).property().content();
                    } else {
                        value = MapUtils.getObject(vertex.elementId().unique(),vertexPropertyLabel);
                    }
                    if(null == value){
                        return;
                    }
                    valueMap.put(property2Field.get(edgePropertyLabel).getValue(),value);
                });
                valueMaps.add(valueMap);
            }
            if(StringUtils.isBlank(linkName)){
                propertyQueryCondtion.getMainCondition().add(QMatch.key(valueMaps));
            }else{
                BaseRowQuery.AssociateMatch associateMatch = new BaseRowQuery.AssociateMatch(QMatch.key(valueMaps),linkName,null);
                propertyQueryCondtion.getAssociateQueryCondition().put(linkName,associateMatch);
            }
        }

        return super.createScanMapping(propertyQueryCondtion,edgeSchema,start,limit);
    }

    private List<ElementMapping> toEdgeScanMapper(final EdgeSchema[] edgeSchemas,
                                                  final Set<QMatch.Q> propertyQuerySet,
                                                  final Collection<EngineVertex> vertices,
                                                  final EdgeDirection direction){
        if(ArrayUtils.isEmpty(edgeSchemas)){
            return Collections.emptyList();
        }
        List<ElementMapping> elementMappings = new ArrayList<>(edgeSchemas.length);
        for(EdgeSchema edgeSchema:edgeSchemas){
            final ElementMapping elementMapping =
                    toMapping(edgeSchema.getLabel(), propertyQuerySet, vertices, direction, null, null);
            if(elementMapping == null){
                continue;
            }
            elementMappings.add(elementMapping);
        }
        return elementMappings;
    }

    public List<ElementMapping> toEdgeScanMapper(final Collection<String> labels,
                                                 final Set<QMatch.Q> propertyQuerySet,
                                                 final Collection<EngineVertex> vertices,
                                                 final EdgeDirection direction) {
        MultiValuedMap<String,EngineVertex> vertexMap = MultiMapUtils.newListValuedHashMap();
        for(EngineVertex vertex:vertices){
            vertexMap.put(vertex.label(),vertex);
        }
        List<ElementMapping> elementMappings = new ArrayList<>(labels.size());
        for(String vertexLabel:vertexMap.keySet()){
            if(direction == EdgeDirection.In || direction == EdgeDirection.InAndOut){
                EdgeSchema[] edgeSchemas = schemaManager().
                        getEdgeSchemaOfVertex(vertexLabel, EdgeDirection.In,labels);
                elementMappings.addAll(toEdgeScanMapper(edgeSchemas,propertyQuerySet,vertexMap.get(vertexLabel),EdgeDirection.In));
            }
            if(direction == EdgeDirection.Out || direction == EdgeDirection.InAndOut){
                EdgeSchema[] edgeSchemas = schemaManager().
                        getEdgeSchemaOfVertex(vertexLabel, EdgeDirection.Out,labels);
                elementMappings.addAll(toEdgeScanMapper(edgeSchemas,propertyQuerySet,vertexMap.get(vertexLabel),EdgeDirection.In));
            }
        }
        return elementMappings;
    }

    GraphVertexId[] getEdgeVertexId(EdgeSchema edgeSchema, RowStore rowStore){
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
