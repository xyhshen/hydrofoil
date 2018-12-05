package org.hydrofoil.core.tinkerpop.glue;

import org.apache.commons.collections.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.hydrofoil.common.graph.GraphEdgeId;
import org.hydrofoil.common.graph.GraphElementId;
import org.hydrofoil.common.graph.GraphElementType;
import org.hydrofoil.common.graph.GraphVertexId;
import org.hydrofoil.common.schema.BaseElementSchema;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.common.util.EncodeUtils;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.core.engine.management.SchemaManager;
import org.json.JSONObject;

import java.util.Collections;
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

    private static final String ID_LABEL_NAME = "label";

    private static final String ID_KEY_NAME = "keys";

    private static final char ID_FIELD_SPLIT = ':';

    private static final String ID_FIELD_WRAP = "\"";

    private static final char[] SPECIAL_CHARACTER = {'\"','\'',':'};

    private static final String JSON_FIRST = "{";

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
        //format:type,order,
        StringBuilder idstring = new StringBuilder();
        if(elementId.unique().size() == 1){
            idstring.append(elementId.label()).append(ID_FIELD_SPLIT);
            idstring.append(EncodeUtils.wrapString(Objects.toString(DataUtils.collectFirst(elementId.unique().values()),null),
                    ID_FIELD_WRAP,SPECIAL_CHARACTER));
        }else{
            //by json
            JSONObject fulljson = new JSONObject();
            fulljson.put(ID_LABEL_NAME,elementId.label());
            JSONObject keyjson = new JSONObject();
            elementId.unique().forEach((k,v)->{
                keyjson.put(k,Objects.toString(v,null));
            });
            fulljson.put(ID_KEY_NAME,keyjson);
            idstring.append(fulljson.toString());
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
    <E extends GraphElementId> E elementId(final Object id,GraphElementType type){
        if(id instanceof GraphElementId){
            return (E)ObjectUtils.clone((GraphElementId) id);
        }
        String idstring = StringUtils.trimToEmpty(Objects.toString(id,null));
        if(StringUtils.isBlank(idstring)){
            return null;
        }
        String label;
        String uniqueKey = null;
        Map<String,Object> uniqueMap = null;
        if(StringUtils.startsWith(idstring,JSON_FIRST)){
            JSONObject fulljson = new JSONObject(idstring);
            label = fulljson.getString(ID_LABEL_NAME);
            final JSONObject keyjson = fulljson.getJSONObject(ID_KEY_NAME);
            uniqueMap = keyjson.toMap();
        }else{
            int index = StringUtils.indexOf(idstring,ID_FIELD_SPLIT);
            label = StringUtils.substring(idstring,0,index);
            uniqueKey = EncodeUtils.unWrapString(StringUtils.substring(idstring,index + 1,idstring.length()),ID_FIELD_WRAP);
        }
        BaseElementSchema elementSchema;
        if(type == GraphElementType.vertex){
            elementSchema = schemaManager.getVertexSchema(label);
        }else{
            elementSchema = schemaManager.getEdgeSchema(label);
        }
        ParameterUtils.notNull(elementSchema,"label");
        if(uniqueMap == null){
            final String key = DataUtils.collectFirst(elementSchema.getPrimaryKeys());
            uniqueMap = Collections.singletonMap(key,uniqueKey);
        }
        final GraphElementId.GraphElementBuilder builder = GraphVertexId.builder(label);
        for(String key:elementSchema.getPrimaryKeys()){
            builder.unique(key, MapUtils.getObject(uniqueMap,key));
        }
        if(type == GraphElementType.vertex){
            return (E) builder.buildVertexId();
        }else{
            return (E) builder.buildEdgeId();
        }
    }

    public GraphVertexId[] vertexIds(Object ...ids){
        if(ArrayUtils.isEmpty(ids)){
            return null;
        }
        GraphVertexId[] elementIds = new GraphVertexId[ids.length];
        for(int i = 0;i < elementIds.length;i++){
            elementIds[i] = elementId(ids[i],GraphElementType.vertex);
        }
        return elementIds;
    }

    public GraphEdgeId[] edgeIds(Object ...ids){
        if(ArrayUtils.isEmpty(ids)){
            return null;
        }
        GraphEdgeId[] elementIds = new GraphEdgeId[ids.length];
        for(int i = 0;i < elementIds.length;i++){
            ids[i] = elementId(ids[i],GraphElementType.edge);
        }
        return elementIds;
    }

}
