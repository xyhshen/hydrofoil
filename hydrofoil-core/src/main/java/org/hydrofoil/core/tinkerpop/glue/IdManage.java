package org.hydrofoil.core.tinkerpop.glue;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.hydrofoil.common.graph.GraphEdgeId;
import org.hydrofoil.common.graph.GraphElementId;
import org.hydrofoil.common.graph.GraphVertexId;
import org.hydrofoil.common.schema.PropertySchema;
import org.hydrofoil.common.util.EncodeUtils;
import org.hydrofoil.core.standard.management.SchemaManager;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

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

    private final AtomicLong lastSchemaChanged;

    /**
     * schema manager
     */
    private final SchemaManager schemaManager;

    private final Map<Integer,String> vertexOrderReverseMap;

    private final Map<Integer,String> edgeOrderReverseMap;

    private final Object lockObject;

    /**
     * element schema order sequence
     */
    static final class ElementSchemaOrder{

        /**
         * sequence
         */
        private Integer order;

        /**
         * unique sequence
         */
        private List<String> uniqueOrder;

        Integer order(){
            return order;
        }

        /**
         * @return String>
         * @see ElementSchemaOrder#uniqueOrder
         **/
        List<String> uniqueOrder() {
            return uniqueOrder;
        }
    }

    /**
     * order
     */
    private Map<String,ElementSchemaOrder> vertexOrderMap;
    //
    private Map<String,ElementSchemaOrder> edgeOrderMap;

    public IdManage(SchemaManager schemaManager){
        this.schemaManager = schemaManager;
        vertexOrderReverseMap = new HashMap<>();
        edgeOrderReverseMap = new HashMap<>();
        vertexOrderMap = new HashMap<>();
        edgeOrderMap = new HashMap<>();
        lastSchemaChanged = new AtomicLong(0L);
        lockObject = new Object();
    }

    private static List<String> getUniquePropertiesOrder(final Map<String,PropertySchema> propertySchemaMap){
        return propertySchemaMap.values().stream().filter(PropertySchema::isPrimary).
                sorted((o1, o2) -> StringUtils.compareIgnoreCase(o1.getLabel(),o2.getLabel())).
                map(PropertySchema::getLabel).collect(Collectors.toList());
    }

    private void loadOrderMap(){
        //check schema is changed
        Long lastChanged = schemaManager.getLastChanged();
        if(!lastSchemaChanged.compareAndSet(lastSchemaChanged.get(),lastChanged)){
            return;
        }

        synchronized(lockObject){
            //calc vertex schema order
            {
                List<String> orders = schemaManager.getVertexSchemaMap().keySet().stream().
                        sorted().collect(Collectors.toList());
                for(int i = 0;i < orders.size();i++){
                    ElementSchemaOrder order = new ElementSchemaOrder();
                    order.order = i;
                    order.uniqueOrder = getUniquePropertiesOrder(schemaManager.getVertexSchemaMap().get(orders.get(i)).getProperties());
                    vertexOrderMap.put(orders.get(i),order);
                    vertexOrderReverseMap.put(i,orders.get(i));
                }
            }
            //calc edge schema order
            {
                List<String> orders = schemaManager.getEdgeSchemaMap().keySet().stream().
                        sorted().collect(Collectors.toList());
                for(int i = 0;i < orders.size();i++){
                    ElementSchemaOrder order = new ElementSchemaOrder();
                    order.order = i;
                    order.uniqueOrder = getUniquePropertiesOrder(schemaManager.getEdgeSchemaMap().get(orders.get(i)).getProperties());
                    edgeOrderMap.put(orders.get(i),order);
                    edgeOrderReverseMap.put(i,orders.get(i));
                }
            }
        }
    }

    @SuppressWarnings("unchecked")
    public Object tinkerpopId(final GraphElementId elementId){
        if(elementId == null) {
            return null;
        }

        //load order map
        loadOrderMap();

        char type = '0';
        ElementSchemaOrder schemaOrder = null;
        if(elementId instanceof GraphVertexId){
            type = ID_PREFIX_VERTEX;
            schemaOrder = vertexOrderMap.get(elementId.label());
        }
        if(elementId instanceof GraphEdgeId){
            type = ID_PREFIX_EDGE;
            schemaOrder = edgeOrderMap.get(elementId.label());
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
        if(id instanceof GraphElementId){
            return (E)ObjectUtils.clone((GraphElementId) id);
        }
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

        GraphElementId.GraphElementBuilder builder;
        //get prefix
        String prefix = idchunks[0];
        //get label order seq
        Integer seq = NumberUtils.toInt(idchunks[1]);
        ElementSchemaOrder schemaOrder;
        boolean vertexOrEdge;
        //check element type
        if(prefix.charAt(0) == ID_PREFIX_VERTEX){
            builder = GraphElementId.builder(vertexOrderReverseMap.get(seq));
            schemaOrder = vertexOrderMap.get(vertexOrderReverseMap.get(seq));
            vertexOrEdge = true;
        }else{
            builder = GraphElementId.builder(edgeOrderReverseMap.get(seq));
            schemaOrder = edgeOrderMap.get(edgeOrderReverseMap.get(seq));
            vertexOrEdge = false;
        }
        //set unique property
        for(int i = 2;i < idchunks.length;i++){
            String label = schemaOrder.uniqueOrder().get(i);
            String value = EncodeUtils.base64Decode(idchunks[i]);
            builder.unique(label,value);
        }
        return vertexOrEdge? (E) builder.buildVertexId() : (E) builder.buildEdgeId();
    }

    public GraphVertexId[] vertexIds(Object ...ids){
        if(ArrayUtils.isEmpty(ids)){
            return null;
        }
        GraphVertexId[] elementIds = new GraphVertexId[ids.length];
        for(int i = 0;i < elementIds.length;i++){
            elementIds[i] = elementId(ids[i]);
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
