package org.hydrofoil.core.standard.internal;

import org.hydrofoil.common.graph.GraphEdgeId;
import org.hydrofoil.common.graph.GraphElementId;
import org.hydrofoil.common.schema.AbstractElementSchema;
import org.hydrofoil.core.management.SchemaManager;

/**
 * EdgeMapper
 * <p>
 * package org.hydrofoil.core.standard.internal
 *
 * @author xie_yh
 * @date 2018/7/15 18:47
 */
public class EdgeMapper extends AbstractElementMapper {
    public EdgeMapper(SchemaManager schemaManager) {
        super(schemaManager);
    }

    @Override
    protected AbstractElementSchema getElementSchema(String label) {
        return schemaManager.getEdgeSchema(label);
    }

    @Override
    protected boolean checkElementIdType(GraphElementId elementId) {
        return elementId instanceof GraphEdgeId;
    }

    public ElementMapping toMapping(GraphEdgeId edgeId){
        return super.toMapping(edgeId,schemaManager.getEdgeSchema(edgeId.label()));
    }

    public ElementMapping toMapping(String label) {
        return null;//super.toMapping(elementId, elementSchema);
    }
}
