package org.hydrofoil.core.management;

import org.dom4j.Element;
import org.hydrofoil.common.configuration.HydrofoilConfiguration;
import org.hydrofoil.common.schema.DataSourceSchema;
import org.hydrofoil.common.schema.EdgeSchema;
import org.hydrofoil.common.schema.TableSchema;
import org.hydrofoil.common.schema.VertexSchema;
import org.hydrofoil.common.util.XmlUtils;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    SchemaManager(){}

    /**
     * load schema
     * @param configuration config data
     */
    public void load(final HydrofoilConfiguration configuration){

    }

    private void loadDataSource(final InputStream is) throws Exception {
        Element root = XmlUtils.getRoot(is);
        List<Element> elements = XmlUtils.listElement(root, ELEMENT_DATASOURCE);
        for(Element element:elements){
            DataSourceSchema dataSourceSchema = new DataSourceSchema();
            dataSourceSchema.read(element);
            dataSourceSchemaMap.put(dataSourceSchema.getDatasourceName(),dataSourceSchema);
        }
    }

    private void loadDataSet(final InputStream is) throws Exception {
        Element root = XmlUtils.getRoot(is);
        List<Element> elements = XmlUtils.listElement(root, ELEMENT_TABLE);
        for(Element element:elements){
            TableSchema tableSchema = new TableSchema();
            tableSchema.read(element);
            tableSchemaMap.put(tableSchema.getName(),tableSchema);
        }
    }

    private void loadMapper(){

    }

    public DataSourceSchema getDatasourceSchema(final String datasSourceName){
        return dataSourceSchemaMap.get(datasSourceName);
    }

    public VertexSchema getVertexSchema(final String label){
        return vertexSchemaMap.get(label);
    }

}
