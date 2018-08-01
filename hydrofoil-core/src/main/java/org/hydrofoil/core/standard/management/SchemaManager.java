package org.hydrofoil.core.standard.management;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.dom4j.Element;
import org.hydrofoil.common.configuration.HydrofoilConfiguration;
import org.hydrofoil.common.configuration.HydrofoilConfigurationItem;
import org.hydrofoil.common.graph.EdgeDirection;
import org.hydrofoil.common.schema.DataSourceSchema;
import org.hydrofoil.common.schema.EdgeSchema;
import org.hydrofoil.common.schema.TableSchema;
import org.hydrofoil.common.schema.VertexSchema;
import org.hydrofoil.common.util.XmlUtils;

import java.io.InputStream;
import java.util.*;

/**
 * SchemaManager
 * <p>
 * package org.hydrofoil.core.management
 *
 * @author xie_yh
 * @date 2018/6/30 11:27
 */
public final class SchemaManager {

    private static final String ELEMENT_DATASOURCE = "datasource";
    private static final String ELEMENT_TABLE = "table";
    private static final String ELEMENT_VERTICES = "vertices";
    private static final String ELEMENT_VERTEX = "vertex";
    private static final String ELEMENT_EDGES = "edges";
    private static final String ELEMENT_EDGE = "edge";

    /**
     * collect source schema map
     */
    private final Map<String,DataSourceSchema> dataSourceSchemaMap;

    /**
     * table schema map
     */
    private final Map<String,TableSchema> tableSchemaMap;

    /**
     * vertex schema
     */
    private final Map<String,VertexSchema> vertexSchemaMap;

    /**
     * edge schema
     */
    private final Map<String,EdgeSchema> edgeSchemaMap;

    /**
     * vertex edge schema map,2nd param as left:in,right:out
     */
    private final Map<String,Pair<Collection<String>,Collection<String>>> vertexEdgeSchemaMap;

    /**
     * last schema changee time
     */
    private volatile Long lastChanged;

    SchemaManager(){
        dataSourceSchemaMap = new HashMap<>();
        tableSchemaMap = new HashMap<>();
        vertexSchemaMap = new HashMap<>();
        edgeSchemaMap = new HashMap<>();
        vertexEdgeSchemaMap = new HashMap<>();
        lastChanged = 0L;
    }

    /**
     * load schema
     * @param configuration config collect
     */
    void load(final HydrofoilConfiguration configuration) throws Exception {
        loadDataSource(configuration.getStream(HydrofoilConfigurationItem.SchemaDatasource));
        loadDataSet(configuration.getStream(HydrofoilConfigurationItem.SchemaDataset));
        loadMapper(configuration.getStream(HydrofoilConfigurationItem.SchemaMapper));
        lastChanged = System.currentTimeMillis();
    }

    private void loadDataSource(final InputStream is) throws Exception {
        Element root = XmlUtils.getRoot(is);
        List<Element> elements = XmlUtils.listElement(root, ELEMENT_DATASOURCE);
        for(Element element:elements){
            DataSourceSchema dataSourceSchema = new DataSourceSchema();
            dataSourceSchema.read(element);
            dataSourceSchemaMap.put(dataSourceSchema.getDatasourceName(),dataSourceSchema);
        }
        IOUtils.closeQuietly(is);
    }

    private void loadDataSet(final InputStream is) throws Exception {
        Element root = XmlUtils.getRoot(is);
        List<Element> elements = XmlUtils.listElement(root, ELEMENT_TABLE);
        for(Element element:elements){
            TableSchema tableSchema = new TableSchema();
            tableSchema.read(element);
            tableSchemaMap.put(tableSchema.getName(),tableSchema);
        }
        IOUtils.closeQuietly(is);
    }

    private void loadVertexMapper(Element root){
        List<Element> elements = XmlUtils.listElement(root.element(ELEMENT_VERTICES),ELEMENT_VERTEX );
        for(Element element:elements){
            VertexSchema vertexSchema = new VertexSchema();
            vertexSchema.read(element);
            vertexSchemaMap.put(vertexSchema.getLabel(),vertexSchema);
        }
    }

    private void loadEdgeMapper(Element root){
        List<Element> elements = XmlUtils.listElement(root.element(ELEMENT_EDGES),ELEMENT_EDGE );
        for(Element element:elements){
            EdgeSchema edgeSchema = new EdgeSchema();
            edgeSchema.read(element);
            edgeSchemaMap.put(edgeSchema.getLabel(),edgeSchema);
        }
        MultiValuedMap<String,EdgeSchema> inMap = new ArrayListValuedHashMap<>();
        MultiValuedMap<String,EdgeSchema> outMap = new ArrayListValuedHashMap<>();
        edgeSchemaMap.forEach((edgeLabel,edgeSchema)->{
            outMap.put(edgeSchema.getSourceLabel(),edgeSchema);
            inMap.put(edgeSchema.getTargetLabel(),edgeSchema);
        });
        vertexSchemaMap.keySet().forEach((vertexLabel)->{
            Collection<EdgeSchema> inSchema = inMap.get(vertexLabel);
            Collection<EdgeSchema> outSchema = outMap.get(vertexLabel);
            Set<String> inLabelSet = new HashSet<>();
            Set<String> outLabelSet = new HashSet<>();
            CollectionUtils.emptyIfNull(inSchema).forEach((v)->inLabelSet.add(v.getLabel()));
            CollectionUtils.emptyIfNull(outSchema).forEach((v)->outLabelSet.add(v.getLabel()));
            vertexEdgeSchemaMap.put(vertexLabel,Pair.of(inLabelSet,outLabelSet));
        });
    }

    private void loadMapper(final InputStream is) throws Exception{
        Element root = XmlUtils.getRoot(is);
        loadVertexMapper(root);
        loadEdgeMapper(root);
        IOUtils.closeQuietly(is);
    }

    /**
     * get collect source schema
     * @param dataSourceName collect source name
     * @return schem
     */
    public DataSourceSchema getDatasourceSchema(final String dataSourceName){
        return dataSourceSchemaMap.get(dataSourceName);
    }

    /**
     * get vertex schema
     * @param label vertex label
     * @return schema
     */
    public VertexSchema getVertexSchema(final String label){
        return vertexSchemaMap.get(label);
    }

    /**
     * get edge schema
     * @param label edge label
     * @return schema
     */
    public EdgeSchema getEdgeSchema(final String label){
        return edgeSchemaMap.get(label);
    }

    /**
     * get edge schema of vertex
     * @param vertexLabel vertex label
     * @param direction direct
     * @param labelSet label set
     * @return schema
     */
    public EdgeSchema[] getEdgeSchemaOfVertex(final String vertexLabel,
                                                    final EdgeDirection direction,
                                                    final Set<String> labelSet){
        Pair<Collection<String>, Collection<String>> vertexEdgePair = vertexEdgeSchemaMap.get(vertexLabel);
        if(vertexEdgePair == null){
            return null;
        }
        Set<String> schemas = new HashSet<>();
        if(direction == EdgeDirection.In){
            schemas.addAll(vertexEdgePair.getLeft());
        }else{
            schemas.addAll(vertexEdgePair.getRight());
        }
        if(schemas.isEmpty()){
            return null;
        }
        final boolean filterLabel = CollectionUtils.isNotEmpty(labelSet);
        List<EdgeSchema> l = new ArrayList<>(schemas.size());
        schemas.stream().filter(p-> !filterLabel || labelSet.contains(p)).
                forEach((v)->l.add(edgeSchemaMap.get(v)));
        if(CollectionUtils.isEmpty(l)){
            return null;
        }
        return l.toArray(new EdgeSchema[schemas.size()]);
    }

    /**
     * @return VertexSchema>
     * @see SchemaManager#vertexSchemaMap
     **/
    public Map<String, VertexSchema> getVertexSchemaMap() {
        return vertexSchemaMap;
    }

    /**
     * @return EdgeSchema>
     * @see SchemaManager#edgeSchemaMap
     **/
    public Map<String, EdgeSchema> getEdgeSchemaMap() {
        return edgeSchemaMap;
    }

    /**
     * @return Long
     * @see SchemaManager#lastChanged
     **/
    public Long getLastChanged() {
        return lastChanged;
    }

    /**
     * get table schema
     * @param name tablename
     * @return schema
     */
    public TableSchema getTableSchema(final String name){
        return tableSchemaMap.get(name);
    }

}
