package org.hydrofoil.core.engine.internal;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.SetUtils;
import org.hydrofoil.common.graph.GraphElementId;
import org.hydrofoil.common.graph.GraphProperty;
import org.hydrofoil.common.graph.GraphVertexId;
import org.hydrofoil.common.graph.QMatch;
import org.hydrofoil.common.provider.datasource.RowQueryScan;
import org.hydrofoil.common.provider.datasource.RowStore;
import org.hydrofoil.common.schema.AbstractElementSchema;
import org.hydrofoil.common.schema.LinkSchema;
import org.hydrofoil.common.schema.PropertySchema;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.common.util.collect.ArrayMap;
import org.hydrofoil.core.engine.EngineElement;
import org.hydrofoil.core.engine.management.SchemaManager;

import java.util.*;

/**
 * ElementMapper
 * <p>
 * package org.hydrofoil.core.engine.internal
 *
 * @author xie_yh
 * @date 2018/7/15 16:59
 */
public abstract class AbstractElementMapper<E extends EngineElement> implements MapperHelper {

    protected final SchemaManager schemaManager;


    public AbstractElementMapper(final SchemaManager schemaManager){
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
    protected abstract AbstractElementSchema getElementSchema(String label);

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

    @SuppressWarnings("unchecked")
    protected ElementMapping toMapping(Set<QMatch.Q> mainCondition, AbstractElementSchema elementSchema,Long start,Long limit){
        ElementMapping elementMapping = new ElementMapping();
        elementMapping.setSchemaItem(elementSchema);
        RowQueryScan rowQueryRequest = new RowQueryScan();
        rowQueryRequest.setName(elementSchema.getTable());
        Map<String,RowQueryScan.AssociateRowQuery> associateRowQueryMap = new TreeMap<>();

        //unique field
        elementSchema.getPrimaryKeys().forEach(v->rowQueryRequest.getUniqueField().add(v));

        //add main query condition
        rowQueryRequest.getMatch().addAll(mainCondition);
        //set property
        elementSchema.getProperties().forEach((propertyLabel,propertySchema)->{
            if(isPropertyInMainTable(propertySchema)) {
                rowQueryRequest.getColumnInformation().column(elementSchema.getTable(),propertySchema.getField());
            }else{
                RowQueryScan.AssociateRowQuery associateRowQuery =
                        associateRowQueryMap.computeIfAbsent(propertySchema.getLinkTable(),
                                (v)->new RowQueryScan.AssociateRowQuery().setName(v));
                if(MapUtils.isEmpty(propertySchema.getChildren())){
                    rowQueryRequest.getColumnInformation().column(associateRowQuery.getName(),propertySchema.getField());
                }else{
                    propertySchema.getChildren().forEach(((name, pairSchema) -> {
                        rowQueryRequest.getColumnInformation().column(associateRowQuery.getName(),pairSchema.getField());
                    }));
                }
                if(associateRowQuery.getMatch().isEmpty()){
                    /*
                        get associate column
                    */
                    final LinkSchema linkSchema = elementSchema.getLinks().get(propertySchema.getLinkTable());
                    linkSchema.getJoinfield().forEach((label,field)->{
                        associateRowQuery.getMatch().add(new RowQueryScan.
                                AssociateMatch(
                                QMatch.eq(field,null),
                                rowQueryRequest.getName(),
                                elementSchema.getProperties().get(label).getField()
                        ));
                        rowQueryRequest.getColumnInformation().column(associateRowQuery.getName(),field);
                    });
                }
            }
        });
        rowQueryRequest.setOffset(start);
        rowQueryRequest.setLimit(limit);
        if(start == null && limit != null){
            rowQueryRequest.setOffset(0L);
        }
        elementMapping.setQueryRequest(rowQueryRequest);
        elementMapping.setDatasource(getDatasourceName(elementSchema.getTable()));
        return elementMapping;
    }

    @SuppressWarnings("unchecked")
    protected <EID extends GraphElementId> EID rowElementToId(AbstractElementSchema elementSchema, RowStore rowStore, Class<EID> clz){
        GraphElementId.GraphElementBuilder builder = GraphElementId.builder(elementSchema.getLabel());
        String tableName = elementSchema.getTable();

        elementSchema.getProperties().values().forEach((propertySchema) -> {
            if(propertySchema.isPrimary()){
                builder.unique(propertySchema.getLabel(),rowStore.value(tableName,propertySchema.getField()));
            }
        });
        return clz.isAssignableFrom(GraphVertexId.class)? (EID) builder.buildVertexId() : (EID) builder.buildEdgeId();
    }

    protected abstract AbstractElementSchema getSchema(String label);

    public boolean checkQueriable(
                                  Collection<String> labels,
                                  Collection<QMatch.Q> propertyQuerySet){
        for(String label:labels){
            final AbstractElementSchema schema = getSchema(label);
            if(propertyQuerySet.stream().
                    filter(query->!checkQueriable(schema,query)).count() > 0){
                return false;
            }
        }
        return true;
    }

    EnginePropertySet rowToProperties(AbstractElementSchema elementSchema,RowStore rowStore){
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
