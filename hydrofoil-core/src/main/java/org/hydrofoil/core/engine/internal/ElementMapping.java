package org.hydrofoil.core.engine.internal;

import org.hydrofoil.common.provider.datasource.RowQueryScan;
import org.hydrofoil.common.schema.SchemaItem;

/**
 * ElementMapping
 * <p>
 * package org.hydrofoil.core.engine.internal
 *
 * @author xie_yh
 * @date 2018/7/14 15:39
 */
public final class ElementMapping{

    private String datasource;

    private SchemaItem schemaItem;

    private RowQueryScan queryRequest;

    private Object context;

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
     * @return RowQueryScan
     * @see ElementMapping#queryRequest
     **/
    public RowQueryScan getQueryRequest() {
        return queryRequest;
    }

    /**
     * @param queryRequest RowQueryScan
     * @see ElementMapping#queryRequest
     **/
    public void setQueryRequest(RowQueryScan queryRequest) {
        this.queryRequest = queryRequest;
    }

    /**
     * @return Object
     * @see ElementMapping#context
     **/
    public Object getContext() {
        return context;
    }

    /**
     * @param context Object
     * @see ElementMapping#context
     **/
    public void setContext(Object context) {
        this.context = context;
    }
}
