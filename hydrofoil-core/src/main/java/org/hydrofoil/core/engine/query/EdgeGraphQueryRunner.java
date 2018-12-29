package org.hydrofoil.core.engine.query;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.ArrayUtils;
import org.hydrofoil.common.graph.EdgeDirection;
import org.hydrofoil.common.provider.datasource.RowStore;
import org.hydrofoil.common.schema.EdgeSchema;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.core.engine.EngineEdge;
import org.hydrofoil.core.engine.EngineElement;
import org.hydrofoil.core.engine.EngineVertex;
import org.hydrofoil.core.engine.internal.AbstractGraphQueryRunner;
import org.hydrofoil.core.engine.internal.ElementMapping;
import org.hydrofoil.core.engine.management.Management;

import java.util.*;
import java.util.stream.Collectors;

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
    private Collection<EngineVertex> vertexSet;

    /**
     * edge direction
     */
    private EdgeDirection direction;

    public EdgeGraphQueryRunner(Management management) {
        super(management);
        this.direction = EdgeDirection.InAndOut;
        this.vertexSet = DataUtils.newHashSetWithExpectedSize(100);
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
            Long start = this.offset;
            Long limit = this.length;
            if(CollectionUtils.isNotEmpty(vertexSet)){
                ParameterUtils.mustTrue(vertexMapper.checkElementIds(vertexSet.stream().map(EngineElement::elementId).collect(Collectors.toSet())),"check vertex id");
                elementRequests.addAll(edgeMapper.toEdgeScanMapper(labels,propertyQuerySet,vertexSet,direction));
            }else{
                /*
                    query vertex by label or other complex style
                */
                Collection<String> finallyLabels;
                if(CollectionUtils.isEmpty(labels)){
                    finallyLabels = management.getSchemaManager().getEdgeSchemaMap().keySet();
                }else{
                    finallyLabels = labels;
                }
                EdgeSchema[] edgeSchemas;
                edgeSchemas = finallyLabels.stream().map(management.
                        getSchemaManager()::getEdgeSchema).toArray(EdgeSchema[]::new);
                if(ArrayUtils.isNotEmpty(edgeSchemas)){
                    for(EdgeSchema schema:edgeSchemas){
                        elementRequests.add(edgeMapper.toMapping(schema.getLabel(),propertyQuerySet,null,EdgeDirection.In,start,limit));
                    }
                }
                elementRequests = elementRequests.stream().filter(Objects::nonNull).collect(Collectors.toList());
            }
        }
        return elementRequests;
    }

    @Override
    protected EngineEdge handleRowToElement(ElementMapping mapping, RowStore rowStore) {
        return edgeMapper.rowStoreToEdge(mapping,(EdgeSchema) mapping.getSchemaItem(),rowStore);
    }

    /**
     * @return GraphElementId
     * @see EdgeGraphQueryRunner#vertexSet
     **/
    public Collection<EngineVertex> vertices() {
        return vertexSet;
    }

    /**
     * @param vertices StandardVertex
     * @see EdgeGraphQueryRunner#vertexSet
     **/
    public EdgeGraphQueryRunner vertices(EngineVertex ...vertices) {
        if(ArrayUtils.isEmpty(vertices)){
            return this;
        }
        for(EngineVertex vertex:vertices){
            if(vertex == null){
                continue;
            }
            vertexSet.add(vertex);
        }
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
