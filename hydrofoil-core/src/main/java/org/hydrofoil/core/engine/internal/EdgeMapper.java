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
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.common.util.bean.KeyValueEntity;
import org.hydrofoil.core.engine.EngineEdge;
import org.hydrofoil.core.engine.EngineProperty;
import org.hydrofoil.core.engine.EngineVertex;
import org.hydrofoil.core.engine.management.SchemaManager;
import org.hydrofoil.core.engine.management.schema.EdgeVertexConnectionInformation;

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
        final EdgeVertexConnectionInformation edgeVertexConnectionInformation =
                (EdgeVertexConnectionInformation) ParameterUtils.notNull(
                schemaManager.getEdgeVertexPropertySet(edgeSchema.getLabel(), sourceVertex));
        return edgeVertexConnectionInformation.isPrimaryKey();
    }

    private class EdgeContext{

        private String label;

        private EdgeDirection direction;

        private Map<KeyValueEntity,GraphVertexId> sourceVertexIds;

        private Map<KeyValueEntity,GraphVertexId> targetVertexIds;

    }

    private class VertexContext{

        private String label;

        private boolean source;

        private String tableName;

        private EdgeVertexConnectionInformation edgeVertexConnectionInformation;
    }

    private ElementMapping createQueryDeriveVertexMapping(
            final boolean source,
            final String vertexLabel,
            final EdgeSchema edgeSchema,
            final EdgeVertexConnectionInformation edgeVertexConnectionInformation,
            final Iterable<RowStore> rows){
        final VertexMapper vertexMapper = new VertexMapper(schemaManager);
        final VertexSchema vertexSchema = schemaManager.getVertexSchema(vertexLabel);
        //get properties is main table
        Set<KeyValueEntity> keyValueEntities = DataUtils.newHashSetWithExpectedSize();
        final Collection<String> vertexFields = edgeVertexConnectionInformation.getVertexFields();
        final KeyValueEntity.KeyValueEntityFactory keyFactory = KeyValueEntity.createFactory(vertexFields);
        String vertexPropertylinkTable = edgeVertexConnectionInformation.getTableName();
        final Collection<Pair<PropertySchema, PropertySchema>> propertySchemas = edgeVertexConnectionInformation.getVertexEdgeProperties();
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
            }
            keyValueEntities.add(keyValue);
        }

        VertexContext vertexContext = new VertexContext();
        //to vertex mapping
        final ElementMapping elementMapping = vertexMapper.toMinimumMapping(vertexSchema.getLabel(), new RowQueryScanKey(vertexPropertylinkTable,
                keyFactory, keyValueEntities), null, null);
        ParameterUtils.notNull(vertexContext);
        elementMapping.setContext(vertexContext);
        vertexContext.tableName = vertexPropertylinkTable;
        vertexContext.label = vertexLabel;
        vertexContext.source = source;
        vertexContext.edgeVertexConnectionInformation = edgeVertexConnectionInformation;

        return elementMapping;
    }

    private Collection<ElementMapping> createDeriveMapping(final ElementMapping mapping, final RowQueryResponse response){
        final EdgeContext edgeContext = (EdgeContext) mapping.getContext();
        final EdgeSchema edgeSchema = schemaManager.getEdgeSchema(edgeContext.label);
        List<ElementMapping> elementMappings = new ArrayList<>();
        if(edgeContext.direction == EdgeDirection.In || edgeContext.targetVertexIds == null){
            elementMappings.add(createQueryDeriveVertexMapping(false,edgeSchema.getTargetLabel(),edgeSchema,
                    schemaManager.getEdgeVertexPropertySet(edgeSchema.getLabel(),false),
                    response.getRows()));
        }
        if(edgeContext.direction == EdgeDirection.Out || edgeContext.sourceVertexIds == null){
            elementMappings.add(createQueryDeriveVertexMapping(true,edgeSchema.getSourceLabel(),edgeSchema,
                    schemaManager.getEdgeVertexPropertySet(edgeSchema.getLabel(),true),
                    response.getRows()));
        }
        return elementMappings;
    }

    private Collection<?> handleDeriveResponse(final ElementMapping mapping, final RowQueryResponse response){
        EdgeContext edgeContext = (EdgeContext) mapping.getBaseMapping().getContext();
        VertexContext vertexContext = (VertexContext) mapping.getContext();
        Map<KeyValueEntity,GraphVertexId> key2VertexIds = DataUtils.newHashMapWithExpectedSize();
        VertexSchema vertexSchema = schemaManager.getVertexSchema(vertexContext.label);
        String table = StringUtils.isBlank(vertexContext.tableName)?vertexSchema.getTable():vertexContext.tableName;
        final KeyValueEntity.KeyValueEntityFactory keyFactory = KeyValueEntity.createFactory(vertexContext.edgeVertexConnectionInformation.getEdgeProperties());
        final Collection<Pair<String, String>> vertexField2EdgeProperty = vertexContext.edgeVertexConnectionInformation.getVertexField2EdgeProperty();
        for(RowStore store:response.getRows()){
            final KeyValueEntity keyValueEntity = keyFactory.create();
            vertexField2EdgeProperty.forEach(pair->{
                keyValueEntity.put(pair.getRight(),store.value(table,pair.getLeft()));
            });
            key2VertexIds.put(keyValueEntity, (GraphVertexId) rowElementToId(vertexSchema,store,GraphVertexId.class));
        }
        if(vertexContext.source){
            edgeContext.sourceVertexIds = key2VertexIds;
        }else{
            edgeContext.targetVertexIds = key2VertexIds;
        }
        return Collections.singleton(edgeContext);
    }

    private Map<KeyValueEntity,GraphVertexId> getVertexPropertyMap(final EdgeSchema edgeSchema,final boolean source,final Collection<EngineVertex> vertices){
        Map<KeyValueEntity,GraphVertexId> keyIds = DataUtils.newHashMapWithExpectedSize(vertices.size());
        final EdgeVertexConnectionInformation edgeVertexConnectionInformation = schemaManager.getEdgeVertexPropertySet(edgeSchema.getLabel(), source);
        KeyValueEntity.KeyValueEntityFactory keyFactory = edgeVertexConnectionInformation.getEdgePropertyFactory();
        for(EngineVertex vertex:vertices){
            final KeyValueEntity keyValueEntity = keyFactory.create();
            final Collection<Pair<PropertySchema, PropertySchema>> vertexEdgeProperties = edgeVertexConnectionInformation.
                    getVertexEdgeProperties();
            for(Pair<PropertySchema, PropertySchema> pair:vertexEdgeProperties){
                final EngineProperty property = vertex.property(pair.getLeft().getLabel());
                if(property == null){
                    continue;
                }
                keyValueEntity.put(pair.getRight().getLabel(),property.property().content());
            }
        }
        return keyIds;
    }

    private ElementMapping wrapLinkQuery(final ElementMapping mapping,final String label,final EdgeDirection direction,final Collection<EngineVertex> vertices){
        final EdgeSchema edgeSchema = schemaManager.getEdgeSchema(label);
        final VertexSchema sourceVertexSchema = schemaManager.getVertexSchema(edgeSchema.getSourceLabel());
        final VertexSchema targetVertexSchema = schemaManager.getVertexSchema(edgeSchema.getTargetLabel());
        if(useOtherContactEdgeProperty(edgeSchema,true)
                &&useOtherContactEdgeProperty(edgeSchema,false)){
            return mapping;
        }

        EdgeContext edgeContext = new EdgeContext();
        edgeContext.label = label;
        edgeContext.direction = direction;
        mapping.setContext(edgeContext);
        mapping.setDeriveFunction(this::createDeriveMapping);
        mapping.setDeriveHandleFunction(this::handleDeriveResponse);

        //set already exist vertex
        if(vertices != null){
            if(direction == EdgeDirection.In){
                edgeContext.targetVertexIds = getVertexPropertyMap(edgeSchema,false,vertices);
            }else{
                edgeContext.sourceVertexIds = getVertexPropertyMap(edgeSchema,true,vertices);
            }
        }

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
            Collection<EdgeSchema.EdgeConnection> connections;
            //is use primary key
            boolean useOtherProperty = useOtherContactEdgeProperty(edgeSchema,direction != EdgeDirection.In);
            //get edge and vertex property
            if(direction == EdgeDirection.In){
                connections = edgeSchema.getTargetConnections();
            }else{
                connections = edgeSchema.getSourceConnections();
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
                connections.forEach((connection)->{
                    Object value;
                    if (useOtherProperty) {
                        value = vertex.property(connection.getVertexPropertyLabel()).property().content();
                    } else {
                        value = MapUtils.getObject(vertex.elementId().unique(),connection.getVertexPropertyLabel());
                    }
                    if(null == value){
                        return;
                    }
                    keyValue.put(property2Field.get(connection.getEdgePropertyLabel()).getValue(),value);
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

    private KeyValueEntity getEdgeProperties(String tableName,Collection<Pair<String,String>> edgeFieldProperty,KeyValueEntity.KeyValueEntityFactory edgePropertyFactory,RowStore rowStore){
        final KeyValueEntity keyValueEntity = edgePropertyFactory.create();
        edgeFieldProperty.forEach(pair->{
            keyValueEntity.put(pair.getRight(),rowStore.value(tableName,pair.getLeft()));
        });
        return keyValueEntity;
    }

    private GraphVertexId[] getEdgeVertexId(ElementMapping mapping, EdgeSchema edgeSchema, RowStore rowStore){
        GraphVertexId[] vertexIds = new GraphVertexId[2];
        String tableName = edgeSchema.getTable();
        final EdgeContext context = (EdgeContext) mapping.getContext();
        GraphElementId.GraphElementBuilder fromBuilder = GraphElementId.builder(edgeSchema.getSourceLabel());
        if(context != null && context.sourceVertexIds != null){
            EdgeVertexConnectionInformation edgeVertexConnectionInformation = schemaManager.getEdgeVertexPropertySet(edgeSchema.getLabel(),true);
            final KeyValueEntity e = getEdgeProperties(edgeVertexConnectionInformation.getTableName(), edgeVertexConnectionInformation.getEdgeFieldProperty(), edgeVertexConnectionInformation.getEdgePropertyFactory(),rowStore);
            vertexIds[0] = context.sourceVertexIds.get(e);
        }else{
            edgeSchema.getSourceConnections().forEach((connection)->{
                fromBuilder.unique(connection.getVertexPropertyLabel(),rowStore.value(tableName,connection.getEdgePropertyLabel()));
            });
            vertexIds[0] = fromBuilder.buildVertexId();
        }

        if(context != null && context.targetVertexIds != null){
            EdgeVertexConnectionInformation edgeVertexConnectionInformation = schemaManager.getEdgeVertexPropertySet(edgeSchema.getLabel(),false);
            final KeyValueEntity e = getEdgeProperties(edgeVertexConnectionInformation.getTableName(), edgeVertexConnectionInformation.getEdgeFieldProperty(), edgeVertexConnectionInformation.getEdgePropertyFactory(),rowStore);
            vertexIds[1] = context.targetVertexIds.get(e);
        }else{
            GraphElementId.GraphElementBuilder toBuilder = GraphElementId.builder(edgeSchema.getTargetLabel());
            edgeSchema.getTargetConnections().forEach((connection)->{
                toBuilder.unique(connection.getVertexPropertyLabel(),rowStore.value(tableName,connection.getEdgePropertyLabel()));
            });
            vertexIds[1] = toBuilder.buildVertexId();
        }

        return vertexIds;
    }

    @SuppressWarnings("unchecked")
    public EngineEdge rowStoreToEdge(ElementMapping mapping,EdgeSchema edgeSchema, RowStore rowStore){
        //make edge id
        GraphVertexId[] vertexIds = getEdgeVertexId(mapping,edgeSchema,rowStore);
        //create edge
        return new EngineEdge(rowElementToId(edgeSchema,rowStore,GraphEdgeId.class),
                vertexIds[0],vertexIds[1],rowToProperties(edgeSchema, rowStore));
    }
}
