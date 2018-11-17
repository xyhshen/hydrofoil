package org.hydrofoil.core.tinkerpop.glue;

import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.hydrofoil.common.graph.GraphEdgeId;
import org.hydrofoil.common.graph.GraphElementId;
import org.hydrofoil.common.graph.GraphVertexId;
import org.hydrofoil.common.schema.BaseElementSchema;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.core.engine.management.SchemaManager;

import java.util.Map;
import java.util.Objects;
import java.util.stream.Stream;

/**
 * IdManage
 * <p>
 * package org.hydrofoil.core.tinkerpop.glue
 *
 * @author xie_yh
 * @date 2018/7/27 15:23
 */
public final class IdManage {

    private static final String ID_PREFIX_VERTEX = "vertex";

    private static final String ID_PREFIX_EDGE = "edge";

    private static final char ID_RAW_SPLIT = '-';

    private static final char ID_FIELD_SPLIT = ':';

    /**
     * schema manager
     */
    private final SchemaManager schemaManager;

    public IdManage(SchemaManager schemaManager){
        this.schemaManager = schemaManager;
    }

    @SuppressWarnings("unchecked")
    public Object tinkerpopId(final GraphElementId elementId){
        if(elementId == null) {
            return null;
        }

        String type = "";
        if(elementId instanceof GraphVertexId){
            type = ID_PREFIX_VERTEX;
        }
        if(elementId instanceof GraphEdgeId){
            type = ID_PREFIX_EDGE;
        }
        //format:type,order,
        StringBuilder idstring = new StringBuilder();
        idstring.append(type).append(ID_RAW_SPLIT).append(elementId.label()).append(ID_RAW_SPLIT);
        if(elementId.unique().size() == 1){
            idstring.append(Objects.toString(DataUtils.collectFirst(elementId.unique().values()),null));
        }else{
            elementId.unique().forEach((k,v)->{
                idstring.append(k).append(ID_FIELD_SPLIT);
                idstring.append(Objects.toString(v,null)).append(ID_RAW_SPLIT);
            });
            idstring.deleteCharAt(idstring.length() - 1);
        }

        return idstring.toString();
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
    public <E extends GraphElementId> E elementId(final Object id){
        if(id instanceof GraphElementId){
            return (E)ObjectUtils.clone((GraphElementId) id);
        }
        String idstring = Objects.toString(id,null);
        if(StringUtils.isBlank(idstring)){
            return null;
        }

        int typeOf = StringUtils.indexOf(idstring,ID_RAW_SPLIT);
        if(typeOf == -1){
            return null;
        }
        String type = StringUtils.substring(idstring,0,typeOf);
        int labelOf = StringUtils.indexOf(idstring,typeOf + 1,ID_RAW_SPLIT);
        if(labelOf == -1){
            return null;
        }
        String label = StringUtils.substring(idstring,typeOf + 1,labelOf);
        BaseElementSchema elementSchema = null;
        if(StringUtils.equalsIgnoreCase(type,ID_PREFIX_VERTEX)){
            elementSchema = schemaManager.getVertexSchema(label);
        }
        if(StringUtils.equalsIgnoreCase(type,ID_PREFIX_EDGE)){
            elementSchema = schemaManager.getEdgeSchema(label);
        }
        if(elementSchema == null){
            return null;
        }
        GraphElementId.GraphElementBuilder builder =
                GraphElementId.builder(elementSchema.getLabel());
        String fieldstring = idstring.substring(labelOf + 1,idstring.length());
        final String[] fieldChunks = StringUtils.split(fieldstring, ID_RAW_SPLIT);
        if(fieldChunks.length <= 0){
            return null;
        }
        if(elementSchema.getPrimaryKeys().size() == 1 &&
                fieldChunks[0].indexOf(ID_FIELD_SPLIT) == -1){
            builder.unique(DataUtils.collectFirst(elementSchema.getPrimaryKeys()),fieldChunks[0]);
        }else{
            Map<String,String> fields = DataUtils.newHashMapWithExpectedSize(elementSchema.getPrimaryKeys().size());
            Stream.of(fieldChunks).forEach(s->{
                String[] fs = StringUtils.split(s,ID_FIELD_SPLIT);
                if(fs.length < 2){
                    return;
                }
                fields.put(fs[0],fs[1]);
            });
            for(String primaryKey:elementSchema.getPrimaryKeys()){
                if(fields.containsKey(primaryKey)){
                    return null;
                }
                builder.unique(primaryKey,fields.get(primaryKey));
            }
        }
        return StringUtils.equalsIgnoreCase(type,ID_PREFIX_VERTEX)?
                (E) builder.buildVertexId() : (E) builder.buildEdgeId();
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
