package org.hydrofoil.core.management;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.dom4j.Element;
import org.hydrofoil.common.configuration.HydrofoilConfiguration;
import org.hydrofoil.common.configuration.HydrofoilConfigurationProperties;
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

    /**
     * data source schema map
     */
    private final Map<String,DataSourceSchema> dataSourceSchemaMap = new HashMap<>();

    /**
     * table schema map
     */
    private final Map<String,TableSchema> tableSchemaMap = new HashMap<>();

    /**
     * vertex schema
     */
    private final Map<String,VertexSchema> vertexSchemaMap = new HashMap<>();

    /**
     * edge schema
     */
    private final Map<String,EdgeSchema> edgeSchemaMap = new HashMap<>();

    /**
     * vertex edge schema map,2nd param as left:in,right:out
     */
    private final Map<String,Pair<String[],String[]>> vertexEdgeSchemaMap = new HashMap<>();

    SchemaManager(){}

    /**
     * load schema
     * @param configuration config data
     */
    public void load(final HydrofoilConfiguration configuration) throws Exception {
        loadDataSource(configuration.getSchemaFile(HydrofoilConfigurationProperties.SCHEMA_DATASOURCE));
        loadDataSet(configuration.getSchemaFile(HydrofoilConfigurationProperties.SCHEMA_DATASET));
        loadMapper(configuration.getSchemaFile(HydrofoilConfigurationProperties.SCHEMA_MAPPER));
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

    private void loadMapper(final InputStream is) throws Exception{
        Element root = XmlUtils.getRoot(is);
        List<Element> elements = XmlUtils.listElement(root.element(ELEMENT_VERTICES),ELEMENT_VERTEX );
        for(Element element:elements){
            VertexSchema vertexSchema = new VertexSchema();
            vertexSchema.read(element);
            vertexSchemaMap.put(vertexSchema.getLabel(),vertexSchema);
        }
        IOUtils.closeQuietly(is);
    }

    /**
     * get data source schema
     * @param dataSourceName data source name
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
     * @return schema
     */
    public EdgeSchema[] getEdgeSchemaOfVertex(final String vertexLabel,
                                                    final EdgeDirection direction,
                                                    final String label){
        Pair<String[], String[]> vertexEdgePair = vertexEdgeSchemaMap.get(vertexLabel);
        if(vertexEdgePair == null){
            return null;
        }
        Set<String> schemas = new HashSet<>();
        if(direction == EdgeDirection.In){
            schemas.addAll(Arrays.asList(vertexEdgePair.getLeft()));
        }else{
            schemas.addAll(Arrays.asList(vertexEdgePair.getRight()));
        }
        if(schemas.isEmpty()){
            return null;
        }
        final boolean filterLabel = StringUtils.isNotBlank(label);
        List<EdgeSchema> l = new ArrayList<>(schemas.size());
        schemas.stream().filter(p-> !filterLabel || StringUtils.equalsIgnoreCase(label, p)).
                forEach((v)->l.add(edgeSchemaMap.get(v)));
        return l.toArray(new EdgeSchema[schemas.size()]);
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
