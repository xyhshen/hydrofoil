package org.hydrofoil.provider.sequence;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.hydrofoil.common.provider.IDataConnectContext;
import org.hydrofoil.common.schema.NamespaceSchema;
import org.hydrofoil.common.schema.TableSchema;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.common.util.LangUtils;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.provider.sequence.loader.LocalFileLoader;
import org.hydrofoil.provider.sequence.reader.CsvFileReader;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * Loaders
 * <p>
 * package org.hydrofoil.provider.sequence
 *
 * @author xie_yh
 * @date 2018/10/17 15:17
 */
public final class Loaders {

    private static final Map<String,Class<? extends IFileLoader>> loaderClassMap;

    private static final Map<String,IFileReader> readerMap;

    static{
        loaderClassMap = DataUtils.newMapWithMaxSize(5);
        loaderClassMap.put("local", LocalFileLoader.class);
        readerMap = DataUtils.newMapWithMaxSize(5);
        readerMap.put("csv", new CsvFileReader());
    }

    private Loaders(){}

    /**
     * load data set,namespace and dataset
     * @param dataSourceContext datsource
     * @return namespace and dataset
     * @throws Exception
     */
    public static Map<String,Map<String,DataSet>> loadDataSet(IDataConnectContext dataSourceContext) throws Exception {
        final Map<String, String> configItems = dataSourceContext.
                getDatasourceSchema().getConfigItems();
        //create file loader
        String datasourceType = MapUtils.getString(configItems,SequenceConfiguration.DATASOURCE_TYPE);
        Class<? extends IFileLoader> loaderClz = loaderClassMap.get(datasourceType);
        ParameterUtils.notNull(loaderClz);
        IFileLoader loader = LangUtils.newInstance(loaderClz);
        ParameterUtils.notNull(loader);
        loader.create(dataSourceContext.getDatasourceSchema());
        //get file list
        Map<String,Map<String,DataSet>> namespaceMap = DataUtils.newHashMapWithExpectedSize(10);
        final Map<String, NamespaceSchema> namespaceSchemaMap = dataSourceContext.getNamespaceSchema();
        final Map<String, TableSchema> tableSchemaMap = dataSourceContext.getTableSchema();
        for(Map.Entry<String,NamespaceSchema> namespaceSchemaEntry:namespaceSchemaMap.entrySet()){
            String filePath = MapUtils.getString(namespaceSchemaEntry.getValue().getOptionMap(),SequenceConfiguration.FILE_PATH);
            String fileType = MapUtils.getString(namespaceSchemaEntry.getValue().getOptionMap(),SequenceConfiguration.FILE_TYPE);
            InputStream is = loader.load(filePath);
            List<FileTable> fileTables = null;
            try{
                IFileReader fileReader = readerMap.get(fileType);
                ParameterUtils.notNull(fileReader);
                fileTables = fileReader.read(is, namespaceSchemaEntry.getValue().getOptionMap());
            }finally {
                IOUtils.closeQuietly(is);
            }
            Map<String,DataSet> dataSetMap = namespaceMap.computeIfAbsent(namespaceSchemaEntry.getKey(),(k)->DataUtils.newMapWithMaxSize(5));
            fileTables.forEach(fileTable -> {
                DataSet dataSet = new DataSet(fileTable);
                //init table by schema
                TableSchema tableSchema = (TableSchema) DataUtils.getOptional(tableSchemaMap.values().stream().
                        filter(p->StringUtils.equalsIgnoreCase(namespaceSchemaEntry.getKey(),p.getNamespace())).
                        filter(p->StringUtils.equalsIgnoreCase(fileTable.getTableName(),p.getRealName())).findFirst());
                dataSet.init(tableSchema);
                dataSetMap.put(fileTable.getTableName(),dataSet);
            });
        }
        return namespaceMap;
    }
}
