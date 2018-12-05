package org.hydrofoil.core.engine.internal;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.SetUtils;
import org.hydrofoil.common.graph.GraphElementId;
import org.hydrofoil.common.graph.GraphProperty;
import org.hydrofoil.common.graph.GraphVertexId;
import org.hydrofoil.common.graph.QMatch;
import org.hydrofoil.common.provider.datasource.*;
import org.hydrofoil.common.schema.BaseElementSchema;
import org.hydrofoil.common.schema.LinkSchema;
import org.hydrofoil.common.schema.PropertySchema;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.common.util.collect.ArrayMap;
import org.hydrofoil.core.engine.EngineElement;
import org.hydrofoil.core.engine.management.SchemaManager;
import org.hydrofoil.core.engine.util.EngineElementUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * ElementMapper
 * <p>
 * package org.hydrofoil.core.engine.internal
 *
 * @author xie_yh
 * @date 2018/7/15 16:59
 */
public abstract class AbstractElementMapper<E extends EngineElement> implements PropertyHeper {

    final SchemaManager schemaManager;


    AbstractElementMapper(final SchemaManager schemaManager){
        this.schemaManager = schemaManager;
    }

    @Override
    public SchemaManager schemaManager(){
        return schemaManager;
    }

    /**
     *
     * @param label
     * @return
     */
    protected abstract BaseElementSchema getElementSchema(String label);

    /**
     *
     * @param elementId
     * @return
     */
    protected abstract boolean checkElementIdType(GraphElementId elementId);

    public boolean checkElementIds(final Collection<GraphElementId> elementIds){
        if(CollectionUtils.isEmpty(elementIds)){
            return false;
        }

        for(final GraphElementId elementId:elementIds){
            if(!checkElementIdType(elementId) || MapUtils.isEmpty(elementId.unique())){
                return false;
            }
            Collection<String> primaryKeys = getElementSchema(elementId.label()).getPrimaryKeys();
            if(!SetUtils.isEqualSet(elementId.unique().keySet(),primaryKeys)){
                return false;
            }
        }
        return true;
    }

    public ElementMapping toGetMapping(String label,Collection<GraphElementId> ids){
        final BaseElementSchema schema = getSchema(label);
        return createGetMappings(schema,ids);
    }

    private ElementMapping createGetMappings(BaseElementSchema elementSchema, Collection<GraphElementId> ids){
        RowQueryGet rowQueryGet = new RowQueryGet();
        rowQueryGet.setName(elementSchema.getTable());
        ids.forEach(id->{
            Map<String,Object> keyValue = DataUtils.newMapWithMaxSize(0);
            elementSchema.getProperties().forEach((k,v)->{
                if(v.isPrimary()){
                    Object value = MapUtils.getObject(id.unique(),v.getLabel());
                    keyValue.put(v.getField(),value);
                }
            });
            rowQueryGet.addRowKey(RowKey.of(keyValue));
        });
        setRowQueryProperties(rowQueryGet,DataUtils.newMapWithMaxSize(0),elementSchema);
        return createMappingElement(rowQueryGet,elementSchema);
    }

    public Map<String,ElementMapping> toGetMappingHasLabel(Collection<GraphElementId> elementIds){
        final MultiValuedMap<String, GraphElementId> idsWithLabel = EngineElementUtils.clusterIdsWithLabel(elementIds);
        Map<String,ElementMapping> mappingMap = DataUtils.newHashMapWithExpectedSize(idsWithLabel.size());
        idsWithLabel.keySet().forEach(label->{
            Collection<GraphElementId> ids = idsWithLabel.get(label);
            final BaseElementSchema schema = getSchema(label);
            mappingMap.put(label,createGetMappings(schema,ids));
        });
        return mappingMap;
    }

    @SuppressWarnings("unchecked")
    ElementMapping createScanMapping(PropertyQueryCondition queryCondition, BaseElementSchema elementSchema, Long start, Long limit){
        RowQueryScan rowQueryRequest = new RowQueryScan();
        rowQueryRequest.setName(elementSchema.getTable());
        Map<String,RowQueryScan.AssociateRowQuery> associateRowQueryMap = new TreeMap<>();

        Set<QMatch.Q> mainCondition = queryCondition.getMainCondition();
        MultiValuedMap<String,BaseRowQuery.AssociateMatch> associateQueryCondition = queryCondition.getAssociateQueryCondition();
        //unique field
        elementSchema.getPrimaryKeys().forEach(v->rowQueryRequest.getUniqueField().add(v));

        //set property
        setRowQueryProperties(rowQueryRequest,associateRowQueryMap,elementSchema);
        //rowQueryRequest.getAssociateQuery().addAll(associateRowQueryMap.values());
        //add main query condition
        rowQueryRequest.getMatch().addAll(mainCondition);

        //set associate query cond
        associateQueryCondition.keySet().forEach(linkTableName->{
            final RowQueryScan.AssociateRowQuery associateRowQuery = associateRowQueryMap.get(linkTableName);
            ParameterUtils.notNull(associateRowQuery);
            associateRowQuery.getMatch().addAll(associateQueryCondition.get(linkTableName));
        });

        rowQueryRequest.setOffset(start);
        rowQueryRequest.setLimit(limit);
        if(start == null && limit != null){
            rowQueryRequest.setOffset(0L);
        }
        return createMappingElement(rowQueryRequest,elementSchema);
    }

    @SuppressWarnings("unchecked")
    <EID extends GraphElementId> EID rowElementToId(BaseElementSchema elementSchema, RowStore rowStore, Class<EID> clz){
        GraphElementId.GraphElementBuilder builder = GraphElementId.builder(elementSchema.getLabel());
        String tableName = elementSchema.getTable();

        elementSchema.getProperties().values().forEach((propertySchema) -> {
            if(propertySchema.isPrimary()){
                builder.unique(propertySchema.getLabel(),rowStore.value(tableName,propertySchema.getField()));
            }
        });
        return clz.isAssignableFrom(GraphVertexId.class)? (EID) builder.buildVertexId() : (EID) builder.buildEdgeId();
    }

    protected abstract BaseElementSchema getSchema(String label);

    public boolean checkQueriable(
                                  Collection<String> labels,
                                  Collection<QMatch.Q> propertyQuerySet){
        for(String label:labels){
            final BaseElementSchema schema = getSchema(label);
            if(propertyQuerySet.stream().
                    filter(query->!checkQueriable(schema,query)).count() > 0){
                return false;
            }
        }
        return true;
    }

    private Object rowToProperty(BaseElementSchema elementSchema,LinkSchema linkSchema,PropertySchema propertySchema, RowStore rowStore){
        if(!propertySchema.hasChildren()){
            //simple property
            Object value = getPropertyValue(elementSchema,linkSchema,null,propertySchema,rowStore);
            return new GraphProperty(propertySchema.getLabel(),value);
        }else{
            //complex property
            final Map<String, PropertySchema> childrenMap = propertySchema.getChildren();
            Map<String,GraphProperty> subPropertyMap = DataUtils.newMapWithMaxSize(childrenMap.size());
            childrenMap.forEach((propertyName,pairSchema)->{
                Object value = getPropertyValue(elementSchema,linkSchema,propertySchema,pairSchema,rowStore);
                GraphProperty subProperty =
                        new GraphProperty(pairSchema.getName(),value);
                subPropertyMap.put(propertyName,subProperty);
            });
            return new ArrayMap<>(subPropertyMap);
        }
    }

    @SuppressWarnings("unchecked")
    EnginePropertySet rowToProperties(BaseElementSchema elementSchema, RowStore rowStore){
        EnginePropertySet.Builder builder = new EnginePropertySet.Builder();
        elementSchema.getProperties().forEach((propertyLabel, propertySchema) -> {
            final LinkSchema linkSchema = elementSchema.getLinks().get(propertySchema.getLinkTable());
            if(linkSchema != null &&
                    linkSchema.isOnlyQuery()){
                return;
            }
            if(linkSchema != null && linkSchema.isOneToMany()){
                //put multi property
                final Collection<RowStore> rows = rowStore.rows(linkSchema.getTable());
                if(propertySchema.hasChildren()){
                    final List<Map<String,GraphProperty>> pl = CollectionUtils.emptyIfNull(rows).stream().map(row ->
                            (Map<String,GraphProperty>) rowToProperty(elementSchema,linkSchema, propertySchema, row)).
                            collect(Collectors.toList());
                    builder.putMaps(propertyLabel,pl);
                }else{
                    final List<GraphProperty> pl = CollectionUtils.emptyIfNull(rows).stream().map(row ->
                            (GraphProperty) rowToProperty(elementSchema,linkSchema, propertySchema, row)).
                            collect(Collectors.toList());
                    builder.puts(propertyLabel,pl);
                }
            }else{
                //put single property
                Object propertyChunk = rowToProperty(elementSchema,linkSchema,propertySchema,rowStore);
                if(propertyChunk instanceof Map){
                    builder.put(propertyLabel, (Map)propertyChunk);
                }else{
                    builder.put(propertyLabel, (GraphProperty) propertyChunk);
                }
            }
        });
        //create property set
        return builder.build();
    }
}


