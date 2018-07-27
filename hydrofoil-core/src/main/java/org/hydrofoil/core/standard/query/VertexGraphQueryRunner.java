package org.hydrofoil.core.standard.query;

import org.apache.commons.collections4.CollectionUtils;
import org.hydrofoil.common.graph.GraphVertexId;
import org.hydrofoil.common.provider.datasource.RowStore;
import org.hydrofoil.common.schema.VertexSchema;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.core.standard.management.Management;
import org.hydrofoil.core.standard.StandardVertex;
import org.hydrofoil.core.standard.internal.AbstractGraphQueryRunner;
import org.hydrofoil.core.standard.internal.ElementMapping;
import org.hydrofoil.core.standard.internal.VertexMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.stream.Collectors;

/**
 * VertexGraphQueryRunner
 * <p>
 * package org.hydrofoil.core.standard.query
 *
 * @author xie_yh
 * @date 2018/7/14 10:06
 */
public final class VertexGraphQueryRunner extends AbstractGraphQueryRunner<StandardVertex,VertexGraphQueryRunner>{

    private VertexMapper vertexMapper;

    public VertexGraphQueryRunner(Management management) {
        super(management);
        this.vertexMapper = new VertexMapper(management.getSchemaManager());
    }

    @Override
    protected Collection<ElementMapping> makeQueryRequest() {
        Collection<ElementMapping> elementRequests = new ArrayList<>(1);

        if(CollectionUtils.isNotEmpty(elementIds)){
            /*
            query vertex by id style
             */
            ParameterUtils.mustTrue(vertexMapper.checkElementIds(elementIds),"check vertex id");
            elementIds.forEach((elementId -> elementRequests.add(vertexMapper.toMapping((GraphVertexId) elementId))));
        }else{
            /*
            query vertex by label or other complex style
             */
            ParameterUtils.mustTrue(!labels.isEmpty(),"vertex label");
            elementRequests.addAll(labels.stream().map((label)-> vertexMapper.
                    toMapping(label,propertyQuerySet,offset,length)).
                    collect(Collectors.toList()));
        }
        return elementRequests;
    }

    @Override
    protected StandardVertex handleRowToElement(ElementMapping mapping, RowStore rowStore) {
        return vertexMapper.rowStoreToVertex((VertexSchema) mapping.getSchemaItem(),rowStore);
    }
}
