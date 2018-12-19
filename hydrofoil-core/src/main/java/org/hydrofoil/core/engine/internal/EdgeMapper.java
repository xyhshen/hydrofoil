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
import org.hydrofoil.common.provider.datasource.RowQueryResponse;
import org.hydrofoil.common.provider.datasource.RowQueryScanKey;
import org.hydrofoil.common.provider.datasource.RowStore;
import org.hydrofoil.common.schema.BaseElementSchema;
import org.hydrofoil.common.schema.EdgeSchema;
import org.hydrofoil.common.schema.PropertySchema;
import org.hydrofoil.common.schema.VertexSchema;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.common.util.bean.KeyValueEntity;
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
            fieldMap = edgeSchema.getSourceProperties();
            label = edgeSchema.getSourceLabel();
        }else{
            fieldMap = edgeSchema.getTargetProperties();
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

    private class EdgeContext{

        private Collection<EngineVertex> vertices;

        private String label;

        private EdgeDirection direction;

    }

    private ElementMapping createQueryDeriveVertexMapping(final String vertexLabel,final EdgeSchema edgeSchema,final Map<String,String> edgeVertexProperies,final Collection<Pair<PropertySchema,PropertySchema>> propertySchemas,final Iterable<RowStore> rows){
        final VertexMapper vertexMapper = new VertexMapper(schemaManager);
        final VertexSchema vertexSchema = schemaManager.getVertexSchema(vertexLabel);
        //get properties is main table
        final boolean inMain = propertiesInMainTable(vertexSchema,edgeVertexProperies.keySet());
        Set<KeyValueEntity> keyValueEntities = DataUtils.newHashSetWithExpectedSize();
        final Set<String> vertexProperties = edgeVertexProperies.keySet();
        final KeyValueEntity.KeyValueEntityFactory keyFactory = KeyValueEntity.createFactory(vertexProperties);
        String vertexPropertylinkTable = null;
        //get all property
        //parse row store
        for(RowStore row:rows){
            final KeyValueEntity keyValue = keyFactory.create();
            for(Pair<PropertySchema,PropertySchema> pair:propertySchemas){
                Object value;
                PropertySchema vertexProperty = pair.getLeft();
                PropertySchema edgeProperty = pair.getRight();
                if(isPropertyInMainTable(edgeProperty)){
                    value = row.value(edgeSchema.getTable(),edgeProperty.getField());
                }else{
                    value = row.value(edgeProperty.getLinkTable(),edgeProperty.getField());
                }
                keyValue.put(vertexProperty.getField(),value);
                vertexPropertylinkTable = vertexProperty.getLinkTable();
            }
            keyValueEntities.add(keyValue);
        }

        //to vertex mapping
        return vertexMapper.toMinimumMapping(vertexSchema.getLabel(),new RowQueryScanKey(vertexPropertylinkTable,keyFactory,keyValueEntities), null, null);
    }

    private Collection<ElementMapping> createDeriveMapping(final ElementMapping mapping, final RowQueryResponse response){
        final EdgeContext edgeContext = (EdgeContext) mapping.getContext();
        final EdgeSchema edgeSchema = schemaManager.getEdgeSchema(edgeContext.label);
        List<ElementMapping> elementMappings = new ArrayList<>();
        if(edgeContext.direction == EdgeDirection.In|| edgeContext.vertices == null){
            elementMappings.add(createQueryDeriveVertexMapping(edgeSchema.getTargetLabel(),edgeSchema,edgeSchema.getTargetProperties(),
                    schemaManager.getEdgeVertexProperties(edgeSchema.getLabel(),false),
                    response.getRows()));
        }
        if(edgeContext.direction == EdgeDirection.Out|| edgeContext.vertices == null){
            elementMappings.add(createQueryDeriveVertexMapping(edgeSchema.getSourceLabel(),edgeSchema,edgeSchema.getSourceProperties(),
                    schemaManager.getEdgeVertexProperties(edgeSchema.getLabel(),true),
                    response.getRows()));
        }
        return elementMappings;
    }

    private Collection<?> handleDeriveResponse(final ElementMapping mapping, final RowQueryResponse response){
        EdgeContext edgeContext = (EdgeContext) mapping.getBaseMapping().getContext();
        Map<KeyValueEntity,GraphVertexId> key2VertexId = DataUtils.newHashMapWithExpectedSize();
        for(RowStore store:response.getRows()){

        }
    }

    private ElementMapping wrapLinkQuery(final ElementMapping mapping,final String label,final EdgeDirection direction,final Collection<EngineVertex> vertices){
        final EdgeSchema edgeSchema = schemaManager.getEdgeSchema(label);
        final VertexSchema sourceVertexSchema = schemaManager.getVertexSchema(edgeSchema.getSourceLabel());
        final VertexSchema targetVertexSchema = schemaManager.getVertexSchema(edgeSchema.getTargetLabel());
        if(CollectionUtils.isEqualCollection(sourceVertexSchema.getPrimaryKeys(),edgeSchema.getSourceProperties().keySet()) &&
                CollectionUtils.isEqualCollection(targetVertexSchema.getPrimaryKeys(),edgeSchema.getTargetProperties().keySet())){
            return mapping;
        }

        EdgeContext edgeContext = new EdgeContext();
        edgeContext.vertices = vertices;
        edgeContext.label = label;
        edgeContext.direction = direction;
        mapping.setContext(edgeContext);
        mapping.setDeriveFunction(this::createDeriveMapping);
        mapping.setDeriveHandleFunction(this::handleDeriveResponse);

        return mapping;
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
                fields = edgeSchema.getTargetProperties();
            }else{
                fields = edgeSchema.getSourceProperties();
            }
            //property to field
            final Map<String, Pair<String,String>> property2Field = getPropertyToFieldMapOfElement(edgeSchema);
            final KeyValueEntity.KeyValueEntityFactory keyFactory = KeyValueEntity.createFactory(property2Field.values().
                    stream().map(Pair::getValue).collect(Collectors.toList()));
            String linkName = null;
            if(property2Field.size() == 1){
                final Pair<String, String> pair = DataUtils.mapFirst(property2Field);
                linkName = pair.getLeft();
            }

            //every vertex get out property convert to edge field
            Set<KeyValueEntity> valueEntities = DataUtils.newHashSetWithExpectedSize(vertices.size());
            for(EngineVertex vertex:vertices) {
                final KeyValueEntity keyValue = keyFactory.create();
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
                    keyValue.put(property2Field.get(edgePropertyLabel).getValue(),value);
                });
                valueEntities.add(keyValue);
            }
            if(StringUtils.isBlank(linkName)){
                propertyQueryCondtion.getMainCondition().add(QMatch.key(valueEntities));
            }else{
                BaseRowQuery.AssociateMatch associateMatch = new BaseRowQuery.AssociateMatch(QMatch.key(valueEntities),linkName,null);
                propertyQueryCondtion.getAssociateQueryCondition().put(linkName,associateMatch);
            }
        }

        final ElementMapping scanMapping = createScanMapping(propertyQueryCondtion, edgeSchema,null, start, limit);
        return wrapLinkQuery(scanMapping,label,direction,vertices);
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
                elementMappings.addAll(toEdgeScanMapper(edgeSchemas,propertyQuerySet,vertexMap.get(vertexLabel),EdgeDirection.Out));
            }
        }
        return elementMappings;
    }

    GraphVertexId[] getEdgeVertexId(EdgeSchema edgeSchema, RowStore rowStore){
        GraphVertexId[] vertexIds = new GraphVertexId[2];
        String tableName = edgeSchema.getTable();
        GraphElementId.GraphElementBuilder fromBuilder = GraphElementId.builder(edgeSchema.getSourceLabel());
        edgeSchema.getSourceProperties().forEach((k, v)->{
            fromBuilder.unique(k,rowStore.value(tableName,v));
        });
        GraphElementId.GraphElementBuilder toBuilder = GraphElementId.builder(edgeSchema.getTargetLabel());
        edgeSchema.getTargetProperties().forEach((k, v)->{
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
