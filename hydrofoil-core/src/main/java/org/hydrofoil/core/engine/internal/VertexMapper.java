package org.hydrofoil.core.engine.internal;

import org.hydrofoil.common.graph.GraphElementId;
import org.hydrofoil.common.graph.GraphVertexId;
import org.hydrofoil.common.graph.QMatch;
import org.hydrofoil.common.provider.datasource.RowStore;
import org.hydrofoil.common.schema.BaseElementSchema;
import org.hydrofoil.common.schema.VertexSchema;
import org.hydrofoil.core.engine.EngineVertex;
import org.hydrofoil.core.engine.management.SchemaManager;

import java.util.Set;

/**
 * VertexMapper
 * <p>
 * package org.hydrofoil.core.engine.internal
 *
 * @author xie_yh
 * @date 2018/7/14 15:36
 */
public final class VertexMapper extends AbstractElementMapper{

    public VertexMapper(final SchemaManager schemaManager){
        super(schemaManager);
    }

    @Override
    protected BaseElementSchema getElementSchema(String label) {
        return schemaManager.getVertexSchema(label);
    }

    @Override
    protected boolean checkElementIdType(GraphElementId elementId) {
        return elementId instanceof GraphVertexId;
    }

    @Override
    protected BaseElementSchema getSchema(String label) {
        return schemaManager.getVertexSchema(label);
    }

    public ElementMapping toMapping(String label, Set<QMatch.Q> propertyQuerySet,Long start,Long limit){
        VertexSchema vertexSchema = schemaManager.getVertexSchema(label);
        //create property query cond
        final PropertyQueryCondition queryCondition = createPropertyQueryCondtion(propertyQuerySet, vertexSchema);
        return createScanMapping(queryCondition,vertexSchema,start,limit);
    }

    public EngineVertex rowStoreToVertex(VertexSchema vertexSchema, RowStore rowStore){
        return new EngineVertex(rowElementToId(vertexSchema,rowStore,GraphVertexId.class),
                rowToProperties(vertexSchema,rowStore));
    }

}
