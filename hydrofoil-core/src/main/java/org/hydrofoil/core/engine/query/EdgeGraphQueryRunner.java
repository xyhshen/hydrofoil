package org.hydrofoil.core.engine.query;

import org.apache.commons.collections4.CollectionUtils;
import org.hydrofoil.common.graph.EdgeDirection;
import org.hydrofoil.common.provider.datasource.RowStore;
import org.hydrofoil.common.schema.EdgeSchema;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.core.engine.EngineEdge;
import org.hydrofoil.core.engine.EngineVertex;
import org.hydrofoil.core.engine.internal.AbstractGraphQueryRunner;
import org.hydrofoil.core.engine.internal.ElementMapping;
import org.hydrofoil.core.engine.management.Management;

import java.util.*;

/**
 * EdgeGraphQueryRunner
 * <p>
 * package org.hydrofoil.core.engine.query
 *
 * @author xie_yh
 * @date 2018/7/14 10:21
 */
public class EdgeGraphQueryRunner extends AbstractGraphQueryRunner<EngineEdge,EdgeGraphQueryRunner> {

    /**
     * Centric vertex
     */
    private EngineVertex vertex;

    /**
     * edge direction
     */
    private EdgeDirection direction;

    public EdgeGraphQueryRunner(Management management) {
        super(management);
        this.direction = EdgeDirection.InAndOut;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected Collection<ElementMapping> makeQueryRequest() {
        List<ElementMapping> elementRequests = new ArrayList<>(1);

        if(CollectionUtils.isNotEmpty(elementIds)){
            /*
            query edge by id style
             */
            ParameterUtils.mustTrue(edgeMapper.checkElementIds(elementIds),"check edge id");
            final Map<String,ElementMapping> map = edgeMapper.toGetMappingHasLabel(elementIds);
            elementRequests.addAll(map.values());
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
                    elementRequests.add(edgeMapper.toMapping(schema.getLabel(),propertyQuerySet,vertex,EdgeDirection.In,start,limit));
                }
            }
            if(outEdgeSchema != null){
                for(EdgeSchema schema:outEdgeSchema){
                    elementRequests.add(edgeMapper.toMapping(schema.getLabel(),propertyQuerySet,vertex,EdgeDirection.Out,null,null));
                }
            }
        }
        return elementRequests;
    }

    @Override
    protected EngineEdge handleRowToElement(ElementMapping mapping, RowStore rowStore) {
        return edgeMapper.rowStoreToEdge((EdgeSchema) mapping.getSchemaItem(),rowStore);
    }

    /**
     * @return GraphElementId
     * @see EdgeGraphQueryRunner#vertex
     **/
    public EngineVertex vertex() {
        return vertex;
    }

    /**
     * @param vertex StandardVertex
     * @see EdgeGraphQueryRunner#vertex
     **/
    public EdgeGraphQueryRunner vertex(EngineVertex vertex) {
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

    @Override
    public boolean operable(OperationType type) {
        if(!super.operable(type)){
            return false;
        }
        return edgeMapper.checkQueriable(labels,propertyQuerySet);
    }
}
