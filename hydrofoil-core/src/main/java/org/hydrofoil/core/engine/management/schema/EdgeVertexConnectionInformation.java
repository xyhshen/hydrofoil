package org.hydrofoil.core.engine.management.schema;

import org.apache.commons.lang3.tuple.Pair;
import org.hydrofoil.common.schema.PropertySchema;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.common.util.bean.KeyValueEntity;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

/**
 * EdgeVertexProperties
 * <p>
 * package org.hydrofoil.core.engine.management.schema
 *
 * @author xie_yh
 * @date 2018/12/20 19:08
 */
public final class EdgeVertexConnectionInformation {

    private Map<String,String> vertex2EdgePropertyFields;

    private Collection<Pair<PropertySchema,PropertySchema>> vertexEdgeProperties;

    private Collection<Pair<String,String>> vertexField2EdgeProperty;

    private Collection<String> edgeProperties;

    private Collection<String> vertexProperties;

    private Collection<Pair<String,String>> edgeFieldProperty;

    private KeyValueEntity.KeyValueEntityFactory edgePropertyFactory;

    private boolean mainTable;

    private String tableName;

    private boolean primaryKey;

    public EdgeVertexConnectionInformation(){
        vertex2EdgePropertyFields = DataUtils.newHashMapWithExpectedSize();
        vertexEdgeProperties = new ArrayList<>();
        vertexField2EdgeProperty = new ArrayList<>();
        edgeProperties = new ArrayList<>();
        vertexProperties = new ArrayList<>();
        edgeFieldProperty = new ArrayList<>();
    }

    /**
     * @return String>
     * @see EdgeVertexConnectionInformation#vertex2EdgePropertyFields
     **/
    public Map<String, String> getVertex2EdgePropertyFields() {
        return vertex2EdgePropertyFields;
    }

    public Collection<String> getVertexFields(){
        return vertex2EdgePropertyFields.keySet();
    }

    public Collection<String> getEdgeFields(){
        return vertex2EdgePropertyFields.values();
    }

    /**
     * @return $field.TypeName
     * @see EdgeVertexConnectionInformation#mainTable
     **/
    public boolean inMainTable() {
        return mainTable;
    }

    /**
     * @param mainTable $field.typeName
     * @see EdgeVertexConnectionInformation#mainTable
     **/
    public void setMainTable(boolean mainTable) {
        this.mainTable = mainTable;
    }

    /**
     * @return PropertySchema>>
     * @see EdgeVertexConnectionInformation#vertexEdgeProperties
     **/
    public Collection<Pair<PropertySchema, PropertySchema>> getVertexEdgeProperties() {
        return vertexEdgeProperties;
    }

    /**
     * @return String>>
     * @see EdgeVertexConnectionInformation#vertexField2EdgeProperty
     **/
    public Collection<Pair<String, String>> getVertexField2EdgeProperty() {
        return vertexField2EdgeProperty;
    }

    /**
     * @return String>
     * @see EdgeVertexConnectionInformation#edgeProperties
     **/
    public Collection<String> getEdgeProperties() {
        return edgeProperties;
    }
    /**
     * @return String
     * @see EdgeVertexConnectionInformation#tableName
     **/
    public String getTableName() {
        return tableName;
    }

    /**
     * @param tableName String
     * @see EdgeVertexConnectionInformation#tableName
     **/
    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    /**
     * @return KeyValueEntityFactory
     * @see EdgeVertexConnectionInformation#edgePropertyFactory
     **/
    public KeyValueEntity.KeyValueEntityFactory getEdgePropertyFactory() {
        return edgePropertyFactory;
    }

    /**
     * @param edgePropertyFactory KeyValueEntityFactory
     * @see EdgeVertexConnectionInformation#edgePropertyFactory
     **/
    public void setEdgePropertyFactory(KeyValueEntity.KeyValueEntityFactory edgePropertyFactory) {
        this.edgePropertyFactory = edgePropertyFactory;
    }

    /**
     * @return String>>
     * @see EdgeVertexConnectionInformation#edgeFieldProperty
     **/
    public Collection<Pair<String, String>> getEdgeFieldProperty() {
        return edgeFieldProperty;
    }

    /**
     * @return String>
     * @see EdgeVertexConnectionInformation#vertexProperties
     **/
    public Collection<String> getVertexProperties() {
        return vertexProperties;
    }

    /**
     * @return $field.TypeName
     * @see EdgeVertexConnectionInformation#primaryKey
     **/
    public boolean isPrimaryKey() {
        return primaryKey;
    }

    /**
     * @param primaryKey $field.typeName
     * @see EdgeVertexConnectionInformation#primaryKey
     **/
    public void setPrimaryKey(boolean primaryKey) {
        this.primaryKey = primaryKey;
    }
}
