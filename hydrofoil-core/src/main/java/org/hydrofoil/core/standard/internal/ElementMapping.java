package org.hydrofoil.core.standard.internal;

import org.hydrofoil.common.provider.datasource.RowQueryRequest;
import org.hydrofoil.common.schema.SchemaItem;

/**
 * ElementMapping
 * <p>
 * package org.hydrofoil.core.standard.internal
 *
 * @author xie_yh
 * @date 2018/7/14 15:39
 */
public final class ElementMapping{

    private String datasource;

    private SchemaItem schemaItem;

    private RowQueryRequest queryRequest;

    /**
     * @return String
     * @see ElementMapping#datasource
     **/
    public String getDatasource() {
        return datasource;
    }

    /**
     * @param datasource String
     * @see ElementMapping#datasource
     **/
    public void setDatasource(String datasource) {
        this.datasource = datasource;
    }

    /**
     * @return SchemaItem
     * @see ElementMapping#schemaItem
     **/
    public SchemaItem getSchemaItem() {
        return schemaItem;
    }

    /**
     * @param schemaItem SchemaItem
     * @see ElementMapping#schemaItem
     **/
    public void setSchemaItem(SchemaItem schemaItem) {
        this.schemaItem = schemaItem;
    }

    /**
     * @return RowQueryRequest
     * @see ElementMapping#queryRequest
     **/
    public RowQueryRequest getQueryRequest() {
        return queryRequest;
    }

    /**
     * @param queryRequest RowQueryRequest
     * @see ElementMapping#queryRequest
     **/
    public void setQueryRequest(RowQueryRequest queryRequest) {
        this.queryRequest = queryRequest;
    }
}
