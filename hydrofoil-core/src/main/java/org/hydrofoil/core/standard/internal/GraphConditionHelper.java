package org.hydrofoil.core.standard.internal;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.SetUtils;
import org.hydrofoil.common.graph.GraphElementId;
import org.hydrofoil.common.graph.GraphVertexId;
import org.hydrofoil.common.schema.PropertySchema;
import org.hydrofoil.core.management.SchemaManager;

import java.util.Collection;
import java.util.Map;

/**
 * GraphConditionUtils
 * <p>
 * package org.hydrofoil.core.standard.internal
 *
 * @author xie_yh
 * @date 2018/7/12 14:32
 */
final class GraphConditionHelper {

    /**
     *
     */
    private final SchemaManager schemaManager;

    GraphConditionHelper(final SchemaManager schemaManager){
        this.schemaManager = schemaManager;
    }

    /**
     * check vertex id
     * @param elementIds id array
     * @return check result
     */
    boolean checkVertexIds(final Collection<GraphElementId> elementIds){
        if(CollectionUtils.isEmpty(elementIds)){
            return false;
        }
        for(final GraphElementId elementId:elementIds){
            if(!(elementId instanceof GraphVertexId) || MapUtils.isEmpty(elementId.unique())){
                return false;
            }
            Map<String, PropertySchema> properties = schemaManager.
                    getVertexSchema(elementId.label()).getProperties();
            if(!SetUtils.isEqualSet(elementId.unique().keySet(),properties.keySet())){
                return false;
            }
        }
        return true;
    }

}
