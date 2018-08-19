package org.hydrofoil.core.standard.internal;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.SetUtils;
import org.hydrofoil.common.graph.GraphElementId;
import org.hydrofoil.common.graph.GraphVertexId;
import org.hydrofoil.common.graph.QMatch;
import org.hydrofoil.common.provider.datasource.RowQueryRequest;
import org.hydrofoil.common.provider.datasource.RowStore;
import org.hydrofoil.common.schema.AbstractElementSchema;
import org.hydrofoil.common.schema.PropertySchema;
import org.hydrofoil.core.standard.StandardElement;
import org.hydrofoil.core.standard.management.SchemaManager;

import java.util.*;

/**
 * ElementMapper
 * <p>
 * package org.hydrofoil.core.standard.internal
 *
 * @author xie_yh
 * @date 2018/7/15 16:59
 */
public abstract class AbstractElementMapper<E extends StandardElement> {

    protected final SchemaManager schemaManager;


    public AbstractElementMapper(final SchemaManager schemaManager){
        this.schemaManager = schemaManager;
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
        RowQueryRequest rowQueryRequest = new RowQueryRequest();
        rowQueryRequest.setName(MapperHelper.getRealTableName(schemaManager,elementSchema.getTable()));
        Map<String,RowQueryRequest.AssociateRowQuery> associateRowQueryMap = new TreeMap<>();

        //add main query condition
        rowQueryRequest.getMatch().addAll(mainCondition);
        //set property
        elementSchema.getProperties().forEach((propertyLabel,propertySchema)->{
            if(MapperHelper.isPropertyInMainTable(propertySchema)) {
                rowQueryRequest.getFields().add(propertySchema.getField());
            }else{
                RowQueryRequest.AssociateRowQuery associateRowQuery =
                        associateRowQueryMap.computeIfAbsent(MapperHelper.getRealTableName(schemaManager,
                                propertySchema.getTable()),(v)->new RowQueryRequest.AssociateRowQuery().setName(v));
                if(MapUtils.isEmpty(propertySchema.getChildren())){
                    associateRowQuery.getFields().add(propertySchema.getField());
                }else{
                    propertySchema.getChildren().forEach(((name, pairSchema) -> {
                        associateRowQuery.getFields().add(pairSchema.getField());
                    }));
                }
                /*
                get associate column
                 */
                propertySchema.getReffield().forEach((label,field)->{
                    associateRowQuery.getMatch().add(new RowQueryRequest.
                            AssociateMatch(
                            QMatch.eq(field,null),
                            rowQueryRequest.getName(),
                            elementSchema.getProperties().get(label).getField()
                    ));
                    associateRowQuery.getFields().add(field);
                });
            }
        });
        rowQueryRequest.setOffset(start);
        rowQueryRequest.setLimit(limit);
        if(start == null && limit != null){
            rowQueryRequest.setOffset(0L);
        }
        elementMapping.setQueryRequest(rowQueryRequest);
        elementMapping.setDatasource(MapperHelper.
                getDatasourceName(schemaManager,elementSchema.getTable()));
        return elementMapping;
    }

    @SuppressWarnings("unchecked")
    protected <EID extends GraphElementId> EID rowElementToId(AbstractElementSchema elementSchema, RowStore rowStore, Class<EID> clz){
        GraphElementId.GraphElementBuilder builder = GraphElementId.builder(elementSchema.getLabel());
        String tableName = MapperHelper.getRealTableName(schemaManager,elementSchema.getTable());

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
                    filter(query->!MapperHelper.checkQueriable(schemaManager,schema,query)).count() > 0){
                return false;
            }
        }
        return true;
    }



}
