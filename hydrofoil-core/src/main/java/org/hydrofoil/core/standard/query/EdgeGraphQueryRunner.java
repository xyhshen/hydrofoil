package org.hydrofoil.core.standard.query;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.hydrofoil.common.graph.EdgeDirection;
import org.hydrofoil.common.graph.GraphEdgeId;
import org.hydrofoil.common.graph.GraphElementId;
import org.hydrofoil.common.provider.datasource.RowStore;
import org.hydrofoil.common.schema.EdgeSchema;
import org.hydrofoil.common.schema.SchemaItem;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.core.management.Management;
import org.hydrofoil.core.standard.StandardEdge;
import org.hydrofoil.core.standard.StandardVertex;
import org.hydrofoil.core.standard.internal.AbstractGraphQueryRunner;
import org.hydrofoil.core.standard.internal.EdgeMapper;
import org.hydrofoil.core.standard.internal.ElementMapping;
import org.hydrofoil.core.standard.internal.VertexMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

/**
 * EdgeGraphQueryRunner
 * <p>
 * package org.hydrofoil.core.standard.query
 *
 * @author xie_yh
 * @date 2018/7/14 10:21
 */
public class EdgeGraphQueryRunner extends AbstractGraphQueryRunner<StandardEdge,EdgeGraphQueryRunner> {

    private EdgeMapper edgeMapper;

    private VertexMapper vertexMapper;

    /**
     * Centric vertex
     */
    private StandardVertex vertex;

    /**
     * edge direction
     */
    private EdgeDirection direction;

    public EdgeGraphQueryRunner(Management management) {
        super(management);
        this.edgeMapper = new EdgeMapper(management.getSchemaManager());
        this.vertexMapper = new VertexMapper(management.getSchemaManager());
    }

    @Override
    protected Collection<ElementMapping> makeQueryRequest() {
        Collection<ElementMapping> elementRequests = new ArrayList<>(1);

        if(CollectionUtils.isNotEmpty(elementIds)){
            /*
            query edge by id style
             */
            ParameterUtils.mustTrue(edgeMapper.checkElementIds(elementIds),"check edge id");
            elementIds.forEach((elementId -> {
                elementRequests.add(edgeMapper.toMapping((GraphEdgeId) elementId));
            }));
        }else if(vertex != null){
            ParameterUtils.mustTrue(vertexMapper.checkElementIds(Collections.singleton(vertex.elementId())),"check vertex id");
            EdgeSchema[] edgeSchemaOfVertex = management.getSchemaManager().
                    getEdgeSchemaOfVertex(vertex.label(), direction != null ? direction : EdgeDirection.InAndOut);
            if(!ArrayUtils.isEmpty(edgeSchemaOfVertex)){

            }
        }else{
            /*
            query vertex by label or other complex style
             */
            ParameterUtils.notBlank(label,"edge label");
        }
        return elementRequests;
    }

    @Override
    protected StandardEdge handleRowToElement(SchemaItem schemaItem, RowStore rowStore) {
        return null;
    }

    /**
     * @return GraphElementId
     * @see EdgeGraphQueryRunner#centreVertexId
     **/
    public StandardVertex vertex() {
        return vertex;
    }

    /**
     * @param vertex StandardVertex
     * @see EdgeGraphQueryRunner#vertex
     **/
    public EdgeGraphQueryRunner vertex(StandardVertex vertex) {
        this.vertex = vertex;
        return this;
    }

    /**
     * @return EdgeDirection
     * @see EdgeGraphQueryRunner#direction
     **/
    public EdgeDirection direction() {
        return direction;
    }

    /**
     * @param direction EdgeDirection
     * @see EdgeGraphQueryRunner#direction
     **/
    public EdgeGraphQueryRunner direction(EdgeDirection direction) {
        this.direction = direction;
        return this;
    }
}
