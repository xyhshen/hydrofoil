package org.hydrofoil.core.standard.management;

import org.apache.commons.io.IOUtils;
import org.hydrofoil.common.provider.IDataProvider;
import org.hydrofoil.common.provider.IDataSource;
import org.hydrofoil.common.provider.IDataSourceContext;
import org.hydrofoil.common.schema.ColumnSchema;
import org.hydrofoil.common.schema.DataSourceSchema;
import org.hydrofoil.common.schema.TableSchema;
import org.hydrofoil.common.util.LangUtils;
import org.hydrofoil.common.util.ParameterUtils;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

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
    private final Map<String,IDataSource> dataSourceMap;

    DataSourceManager(final Management management){
        this.management = management;
        this.dataSourceMap = new HashMap<>();
    }

    /**
     * geet collect source
     * @param dataSourceName source name
     * @return collect source
     */
    public IDataSource getDatasource(final String dataSourceName){
        synchronized (dataSourceMap){
            return dataSourceMap.computeIfAbsent(dataSourceName,(name)->{
                DataSourceSchema datasourceSchema = management.getSchemaManager().
                        getDatasourceSchema(name);
                ParameterUtils.notNull(datasourceSchema,"collect source schema" + name);
                IDataProvider provider = loadProvider(datasourceSchema.getProvider());
                IDataSource connect = provider.connect(
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

class DefaultDatasourceContext implements IDataSourceContext{

    private DataSourceSchema dataSourceSchema;

    private SchemaManager schemaManager;

    DefaultDatasourceContext(
            SchemaManager schemaManager,
            DataSourceSchema dataSourceSchema){
        this.schemaManager = schemaManager;
        this.dataSourceSchema = dataSourceSchema;
    }

    @Override
    public ColumnSchema getColumnSchema(String tableName, String columnName) {
        final TableSchema tableSchema = schemaManager.getTableSchema(tableName);
        if(tableName == null){
            return null;
        }
        return tableSchema.getColumns().get(columnName);
    }

    @Override
    public DataSourceSchema getDatasourceSchema() {
        return dataSourceSchema;
    }
}
