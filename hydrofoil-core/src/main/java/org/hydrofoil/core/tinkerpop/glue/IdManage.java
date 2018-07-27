package org.hydrofoil.core.tinkerpop.glue;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.hydrofoil.common.graph.GraphEdgeId;
import org.hydrofoil.common.graph.GraphElementId;
import org.hydrofoil.common.graph.GraphVertexId;
import org.hydrofoil.common.util.EncodeUtils;
import org.hydrofoil.core.standard.management.SchemaManager;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * IdManage
 * <p>
 * package org.hydrofoil.core.tinkerpop.glue
 *
 * @author xie_yh
 * @date 2018/7/27 15:23
 */
public final class IdManage {

    private static final char ID_PREFIX_VERTEX = 'v';

    private static final char ID_PREFIX_EDGE = 'e';

    private static final char ID_RAW_SPLIT = '-';

    /**
     * schema manager
     */
    private final SchemaManager schemaManager;

    private final Map<Integer,String> vertexOrderReverseMap;

    private final Map<Integer,String> edgeOrderReverseMap;

    public IdManage(SchemaManager schemaManager){
        this.schemaManager = schemaManager;
        vertexOrderReverseMap = new HashMap<>();
        edgeOrderReverseMap = new HashMap<>();
    }

    private synchronized void loadOrderMap(){
        if(schemaManager.getVertexOrder().size() !=
                vertexOrderReverseMap.size()){

        }
        if(schemaManager.getEdgeOrder().size() !=
                edgeOrderReverseMap.size()){

        }
    }

    @SuppressWarnings("unchecked")
    public Object tinkerpopId(final GraphElementId elementId){
        if(elementId == null){
            return null;
        }
        char type = '0';
        SchemaManager.ElementSchemaOrder schemaOrder = null;
        if(elementId instanceof GraphVertexId){
            type = ID_PREFIX_VERTEX;
            schemaOrder = schemaManager.getVertexOrder().get(elementId.label());
        }
        if(elementId instanceof GraphEdgeId){
            type = ID_PREFIX_EDGE;
            schemaOrder = schemaManager.getEdgeOrder().get(elementId.label());
        }
        if(schemaOrder == null){
            return null;
        }
        //format:type,order,
        StringBuilder idstring = new StringBuilder();
        idstring.append(type).append(schemaOrder.order());
        StringBuilder uniquestring = new StringBuilder();
        for(String label:schemaOrder.uniqueOrder()){
            uniquestring.append(ID_RAW_SPLIT).append(EncodeUtils.
                    base64(MapUtils.getString(elementId.unique(),label)));
        }
        return idstring.append(uniquestring);
    }

    public Object[] tinkerpopIds(final GraphElementId ...elementIds){
        if(ArrayUtils.isEmpty(elementIds)){
            return null;
        }
        Object[] ids = new Object[elementIds.length];
        for(int i = 0;i < elementIds.length;i++){
            ids[i] = tinkerpopId(elementIds[i]);
        }
        return ids;
    }

    @SuppressWarnings("unchecked")
    public <E extends GraphElementId> E elementId(Object id){
        String idstring = Objects.toString(id,null);
        if(StringUtils.isBlank(idstring)){
            return null;
        }
        String[] idchunks = StringUtils.split(idstring, "-");
        if(ArrayUtils.isEmpty(idchunks) || idchunks.length < 2){
            return null;
        }

        //re load order map
        loadOrderMap();

        E elementId;
        //get prefix
        String prefix = idchunks[0];
        //get label order seq
        Integer seq = NumberUtils.toInt(idchunks[1]);
        SchemaManager.ElementSchemaOrder schemaOrder;
        //check element type
        if(prefix.charAt(0) == ID_PREFIX_VERTEX){
            elementId = (E) new GraphVertexId(vertexOrderReverseMap.get(seq));
            schemaOrder = schemaManager.getVertexOrder().get(elementId.label());
        }else{
            elementId = (E) new GraphEdgeId(edgeOrderReverseMap.get(seq));
            schemaOrder = schemaManager.getEdgeOrder().get(elementId.label());
        }
        //set unique property
        for(int i = 2;i < idchunks.length;i++){
            String label = schemaOrder.uniqueOrder().get(i);
            String value = EncodeUtils.base64Decode(idchunks[i]);
            elementId.unique(label,value);
        }

        return elementId;
    }

    public GraphVertexId[] vertexIds(Object ...ids){
        if(ArrayUtils.isEmpty(ids)){
            return null;
        }
        GraphVertexId[] elementIds = new GraphVertexId[ids.length];
        for(int i = 0;i < elementIds.length;i++){
            ids[i] = elementId(ids[i]);
        }
        return elementIds;
    }

    public GraphEdgeId[] edgeIds(Object ...ids){
        if(ArrayUtils.isEmpty(ids)){
            return null;
        }
        GraphEdgeId[] elementIds = new GraphEdgeId[ids.length];
        for(int i = 0;i < elementIds.length;i++){
            ids[i] = elementId(ids[i]);
        }
        return elementIds;
    }

}
