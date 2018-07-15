package org.hydrofoil.core.standard.internal;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.SetUtils;
import org.hydrofoil.common.graph.GraphElementId;
import org.hydrofoil.common.graph.QMatch;
import org.hydrofoil.common.provider.datasource.RowQueryRequest;
import org.hydrofoil.common.schema.AbstractElementSchema;
import org.hydrofoil.common.schema.PropertySchema;
import org.hydrofoil.core.management.SchemaManager;
import org.hydrofoil.core.standard.StandardElement;

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
    public ElementMapping toMapping(GraphElementId elementId, AbstractElementSchema elementSchema){
        ElementMapping elementMapping = new ElementMapping();
        elementMapping.setSchemaItem(elementSchema);
        RowQueryRequest rowQueryRequest = new RowQueryRequest();
        rowQueryRequest.setName(MapperHelper.getRealTableName(schemaManager,elementSchema.getTable()));
        Map<String,RowQueryRequest.AssociateRowQuery> associateRowQueryMap = new TreeMap<>();

        elementSchema.getProperties().forEach((propertyLabel,propertySchema)->{
            if(propertySchema.isPrimary()){
                Object value = MapUtils.getObject(elementId.unique(),propertySchema.getLabel());
                rowQueryRequest.getMatch().add(QMatch.eq(propertySchema.getField(),value));
            }
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
        rowQueryRequest.setStart(0L);
        rowQueryRequest.setLimit(1L);
        elementMapping.setQueryRequest(rowQueryRequest);
        elementMapping.setDatasource(MapperHelper.
                getDatasourceName(schemaManager,elementSchema.getTable()));
        return elementMapping;
    }



}
