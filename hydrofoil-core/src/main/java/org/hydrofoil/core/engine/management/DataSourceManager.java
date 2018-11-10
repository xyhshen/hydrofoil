package org.hydrofoil.core.engine.management;

import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.ArrayUtils;
import org.hydrofoil.common.provider.IDataProvider;
import org.hydrofoil.common.provider.IDataConnector;
import org.hydrofoil.common.provider.IDataConnectContext;
import org.hydrofoil.common.schema.ColumnSchema;
import org.hydrofoil.common.schema.DataSourceSchema;
import org.hydrofoil.common.schema.NamespaceSchema;
import org.hydrofoil.common.schema.TableSchema;
import org.hydrofoil.common.util.LangUtils;
import org.hydrofoil.common.util.ParameterUtils;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Stream;

/**
 * DataProviderManager
 * <p>
 * package org.hydrofoil.core.management
 *
 * @author xie_yh
 * @date 2018/7/2 16:06
 */
public final class DataSourceManager implements Closeable {

    /**
     * graph managerment
     */
    private final Management management;

    /**
     * collect source map
     */
    private final Map<String,IDataConnector> dataSourceMap;

    DataSourceManager(final Management management){
        this.management = management;
        this.dataSourceMap = new HashMap<>();
    }

    /**
     * geet collect source
     * @param dataSourceName source name
     * @return collect source
     */
    public IDataConnector getDatasource(final String dataSourceName){
        synchronized (dataSourceMap){
            return dataSourceMap.computeIfAbsent(dataSourceName,(name)->{
                DataSourceSchema datasourceSchema = management.getSchemaManager().
                        getDatasourceSchema(name);
                ParameterUtils.notNull(datasourceSchema,"collect source schema" + name);
                IDataProvider provider = loadProvider(datasourceSchema.getProvider());
                IDataConnector connect = provider.connect(
                        new DefaultDatasourceContext(management.getSchemaManager(),
                        datasourceSchema));
                ParameterUtils.nullMessage(connect,"collect source " + name
                        + " connect failed");
                return connect;
            });
        }
    }

    /**
     * load provider
     * @param provider provider
     * @return provider
     */
    private IDataProvider loadProvider(String provider){
        IDataProvider p = LangUtils.newInstance(Thread.currentThread().
                getContextClassLoader(), IDataProvider.DATA_PROVIDER_CLASS_PATH + "." +
                provider + "." + IDataProvider.DATA_PROVIDER_CLASS_NAME);
        ParameterUtils.notNull(p,"provider instance " + provider);
        return p;
    }

    @Override
    public void close() throws IOException {
        //close all datasource
        synchronized (dataSourceMap){
            dataSourceMap.values().forEach(IOUtils::closeQuietly);
            dataSourceMap.clear();
        }
    }
}

class DefaultDatasourceContext implements IDataConnectContext {

    private DataSourceSchema dataSourceSchema;

    private SchemaManager schemaManager;

    DefaultDatasourceContext(
            SchemaManager schemaManager,
            DataSourceSchema dataSourceSchema){
        this.schemaManager = schemaManager;
        this.dataSourceSchema = dataSourceSchema;
    }

    @Override
    public ColumnSchema getColumnSchema(final String tableName, final String columnName) {
        final TableSchema tableSchema = schemaManager.getTableSchema(tableName);
        if(tableName == null){
            return null;
        }
        return tableSchema.getColumns().get(columnName);
    }

    @Override
    public Map<String, TableSchema> getTableSchema(final String... tableNames) {
        final Map<String,TableSchema> map = new TreeMap<>();
        if(ArrayUtils.isEmpty(tableNames)){
            final Set<String> datasourceTable = schemaManager.
                    getDatasourceTable(dataSourceSchema.getDatasourceName());
            datasourceTable.forEach(name-> map.put(name,schemaManager.getTableSchema(name)));
        }else{
            Stream.of(tableNames).forEach(name-> map.put(name,schemaManager.getTableSchema(name)));
        }
        return map;
    }

    @Override
    public Map<String, NamespaceSchema> getNamespaceSchema(String... namespaces) {
        final Map<String,NamespaceSchema> map = new TreeMap<>();
        if(ArrayUtils.isEmpty(namespaces)){
            final Set<String> datasourceNamespace = schemaManager.
                    getDatasourceNamespace(dataSourceSchema.getDatasourceName());
            datasourceNamespace.forEach(name-> map.put(name,schemaManager.getNamespaceSchema(name)));
        }else{
            Stream.of(namespaces).forEach(name-> map.put(name,schemaManager.getNamespaceSchema(name)));
        }
        return map;
    }

    @Override
    public DataSourceSchema getDatasourceSchema() {
        return dataSourceSchema;
    }
}
