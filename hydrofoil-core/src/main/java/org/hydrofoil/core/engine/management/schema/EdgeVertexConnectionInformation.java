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

    private KeyValueEntity.KeyValueEntityFactory edgeFieldFactory;

    private boolean vertexInMainTable;

    private String vertexTableName;

    private boolean vertexPrimaryKey;

    private boolean edgeInMainTable;

    private String edgeTableName;

    private boolean edgePrimaryKey;

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
     * @see EdgeVertexConnectionInformation#vertexTableName
     **/
    public String getVertexTableName() {
        return vertexTableName;
    }

    /**
     * @param vertexTableName String
     * @see EdgeVertexConnectionInformation#vertexTableName
     **/
    public void setVertexTableName(String vertexTableName) {
        this.vertexTableName = vertexTableName;
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
     * @see EdgeVertexConnectionInformation#vertexInMainTable
     **/
    public boolean isVertexInMainTable() {
        return vertexInMainTable;
    }

    /**
     * @param vertexInMainTable $field.typeName
     * @see EdgeVertexConnectionInformation#vertexInMainTable
     **/
    public void setVertexInMainTable(boolean vertexInMainTable) {
        this.vertexInMainTable = vertexInMainTable;
    }

    /**
     * @return $field.TypeName
     * @see EdgeVertexConnectionInformation#vertexPrimaryKey
     **/
    public boolean isVertexPrimaryKey() {
        return vertexPrimaryKey;
    }

    /**
     * @param vertexPrimaryKey $field.typeName
     * @see EdgeVertexConnectionInformation#vertexPrimaryKey
     **/
    public void setVertexPrimaryKey(boolean vertexPrimaryKey) {
        this.vertexPrimaryKey = vertexPrimaryKey;
    }

    /**
     * @return $field.TypeName
     * @see EdgeVertexConnectionInformation#edgeInMainTable
     **/
    public boolean isEdgeInMainTable() {
        return edgeInMainTable;
    }

    /**
     * @param edgeInMainTable $field.typeName
     * @see EdgeVertexConnectionInformation#edgeInMainTable
     **/
    public void setEdgeInMainTable(boolean edgeInMainTable) {
        this.edgeInMainTable = edgeInMainTable;
    }

    /**
     * @return String
     * @see EdgeVertexConnectionInformation#edgeTableName
     **/
    public String getEdgeTableName() {
        return edgeTableName;
    }

    /**
     * @param edgeTableName String
     * @see EdgeVertexConnectionInformation#edgeTableName
     **/
    public void setEdgeTableName(String edgeTableName) {
        this.edgeTableName = edgeTableName;
    }

    /**
     * @return $field.TypeName
     * @see EdgeVertexConnectionInformation#edgePrimaryKey
     **/
    public boolean isEdgePrimaryKey() {
        return edgePrimaryKey;
    }

    /**
     * @param edgePrimaryKey $field.typeName
     * @see EdgeVertexConnectionInformation#edgePrimaryKey
     **/
    public void setEdgePrimaryKey(boolean edgePrimaryKey) {
        this.edgePrimaryKey = edgePrimaryKey;
    }

    /**
     * @return KeyValueEntityFactory
     * @see EdgeVertexConnectionInformation#edgeFieldFactory
     **/
    public KeyValueEntity.KeyValueEntityFactory getEdgeFieldFactory() {
        return edgeFieldFactory;
    }

    /**
     * @param edgeFieldFactory KeyValueEntityFactory
     * @see EdgeVertexConnectionInformation#edgeFieldFactory
     **/
    public void setEdgeFieldFactory(KeyValueEntity.KeyValueEntityFactory edgeFieldFactory) {
        this.edgeFieldFactory = edgeFieldFactory;
    }
}
