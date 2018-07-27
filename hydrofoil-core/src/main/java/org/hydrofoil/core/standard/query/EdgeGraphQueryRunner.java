package org.hydrofoil.core.standard.query;

import org.apache.commons.collections4.CollectionUtils;
import org.hydrofoil.common.graph.EdgeDirection;
import org.hydrofoil.common.graph.GraphEdgeId;
import org.hydrofoil.common.provider.datasource.RowStore;
import org.hydrofoil.common.schema.EdgeSchema;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.core.standard.management.Management;
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
        this.direction = EdgeDirection.InAndOut;
    }

    @Override
    protected Collection<ElementMapping> makeQueryRequest() {
        Collection<ElementMapping> elementRequests = new ArrayList<>(1);

        if(CollectionUtils.isNotEmpty(elementIds)){
            /*
            query edge by id style
             */
            ParameterUtils.mustTrue(edgeMapper.checkElementIds(elementIds),"check edge id");
            elementIds.forEach((elementId->elementRequests.add(edgeMapper.toMapping((GraphEdgeId) elementId))));
        }else{
            EdgeSchema[] inEdgeSchema = null;
            EdgeSchema[] outEdgeSchema = null;
            Long start = this.offset;
            Long limit = this.length;
            if(vertex != null){
                ParameterUtils.mustTrue(vertexMapper.checkElementIds(Collections.singleton(vertex.elementId())),"check vertex id");
                if(direction == EdgeDirection.In || direction == EdgeDirection.InAndOut){
                    inEdgeSchema = management.getSchemaManager().
                            getEdgeSchemaOfVertex(vertex.label(), EdgeDirection.In,labels);
                }
                if(direction == EdgeDirection.Out || direction == EdgeDirection.InAndOut){
                    outEdgeSchema = management.getSchemaManager().
                            getEdgeSchemaOfVertex(vertex.label(), EdgeDirection.Out,labels);
                }
                start = limit = null;
            }else{
                /*
                    query vertex by label or other complex style
                */
                ParameterUtils.mustTrue(!labels.isEmpty(),"edge label");
                inEdgeSchema = (EdgeSchema[]) labels.stream().map(management.
                        getSchemaManager()::getEdgeSchema).toArray();
            }

            if(inEdgeSchema != null){
                for(EdgeSchema schema:inEdgeSchema){
                    elementRequests.add(edgeMapper.toMapping(propertyQuerySet,vertex,schema,EdgeDirection.In,start,limit));
                }
            }
            if(outEdgeSchema != null){
                for(EdgeSchema schema:outEdgeSchema){
                    elementRequests.add(edgeMapper.toMapping(propertyQuerySet,vertex,schema,EdgeDirection.Out,null,null));
                }
            }
        }
        return elementRequests;
    }

    @Override
    protected StandardEdge handleRowToElement(ElementMapping mapping, RowStore rowStore) {
        return edgeMapper.rowStoreToEdge((EdgeSchema) mapping.getSchemaItem(),rowStore);
    }

    /**
     * @return GraphElementId
     * @see EdgeGraphQueryRunner#vertex
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
