package org.hydrofoil.core.engine.internal;

import org.hydrofoil.common.provider.datasource.BaseRowQuery;
import org.hydrofoil.common.provider.datasource.RowQueryResponse;
import org.hydrofoil.common.schema.SchemaItem;

import java.util.Collection;
import java.util.function.BiFunction;

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

    private BaseRowQuery queryRequest;

    private Object context;

    private BiFunction<ElementMapping,RowQueryResponse,Collection<ElementMapping>> deriveFunction;

    private BiFunction<ElementMapping,RowQueryResponse,Collection<?>> deriveHandleFunction;

    private ElementMapping baseMapping;

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
    public BaseRowQuery getQueryRequest() {
        return queryRequest;
    }

    /**
     * @param queryRequest RowQueryScan
     * @see ElementMapping#queryRequest
     **/
    public void setQueryRequest(BaseRowQuery queryRequest) {
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

    /**
     * @return ElementMapping>
     * @see ElementMapping#deriveFunction
     **/
    public BiFunction<ElementMapping, RowQueryResponse, Collection<ElementMapping>> getDeriveFunction() {
        return deriveFunction;
    }

    /**
     * @param deriveFunction ElementMapping>
     * @see ElementMapping#deriveFunction
     **/
    public void setDeriveFunction(BiFunction<ElementMapping, RowQueryResponse, Collection<ElementMapping>> deriveFunction) {
        this.deriveFunction = deriveFunction;
    }

    /**
     * @return Collection<?>>
     * @see ElementMapping#deriveHandleFunction
     **/
    public BiFunction<ElementMapping, RowQueryResponse, Collection<?>> getDeriveHandleFunction() {
        return deriveHandleFunction;
    }

    /**
     * @param deriveHandleFunction Collection<?>>
     * @see ElementMapping#deriveHandleFunction
     **/
    public void setDeriveHandleFunction(BiFunction<ElementMapping, RowQueryResponse, Collection<?>> deriveHandleFunction) {
        this.deriveHandleFunction = deriveHandleFunction;
    }

    /**
     * @return ElementMapping
     * @see ElementMapping#baseMapping
     **/
    public ElementMapping getBaseMapping() {
        return baseMapping;
    }

    /**
     * @param baseMapping ElementMapping
     * @see ElementMapping#baseMapping
     **/
    public void setBaseMapping(ElementMapping baseMapping) {
        this.baseMapping = baseMapping;
    }
}
