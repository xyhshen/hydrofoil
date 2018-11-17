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
import org.hydrofoil.common.schema.PropertySchema;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.common.util.collect.ArrayMap;
import org.hydrofoil.core.engine.EngineElement;
import org.hydrofoil.core.engine.management.SchemaManager;
import org.hydrofoil.core.engine.util.EngineElementUtils;

import java.util.*;

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
            Set<String> unique = new HashSet<>();
            Map<String, PropertySchema> properties = getElementSchema(elementId.label()).getProperties();
            properties.values().forEach((v)->{
                if(v.isPrimary()){
                    unique.add(v.getLabel());
                }
            });
            if(!SetUtils.isEqualSet(elementId.unique().keySet(),unique)){
                return false;
            }
        }
        return true;
    }

    protected ElementMapping toGetMapping(String label,Collection<GraphElementId> ids){
        final BaseElementSchema schema = getSchema(label);
        return createGetMappings(schema,ids);
    }

    private ElementMapping createGetMappings(BaseElementSchema elementSchema, Collection<GraphElementId> ids){
        RowQueryGet rowQueryGet = new RowQueryGet();
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

    protected Map<String,ElementMapping> toGetMappingHasLabel(Collection<GraphElementId> elementIds){
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

    EnginePropertySet rowToProperties(BaseElementSchema elementSchema, RowStore rowStore){
        EnginePropertySet.Builder builder = new EnginePropertySet.Builder();
        elementSchema.getProperties().forEach((propertyLabel, propertySchema) -> {
            if(CollectionUtils.sizeIsEmpty(propertySchema.getChildren())){
                //simple property
                Object value = getPropertyValue(elementSchema,propertySchema,rowStore);
                GraphProperty graphProperty =
                        new GraphProperty(propertySchema.getLabel(),value);
                builder.put(propertyLabel,graphProperty);
            }else{
                //complex property
                final Map<String, PropertySchema> childrenMap = propertySchema.getChildren();
                Map<String,GraphProperty> subPropertyMap = DataUtils.newMapWithMaxSize(childrenMap.size());
                childrenMap.forEach((propertyName,pairSchema)->{
                    Object value = getPropertyValue(elementSchema,pairSchema,rowStore);
                    GraphProperty subProperty =
                            new GraphProperty(propertySchema.getName(),value);
                    subPropertyMap.put(propertyName,subProperty);
                });
                builder.put(propertyLabel, new ArrayMap<>(subPropertyMap));
            }
        });
        return builder.build();
    }
}


