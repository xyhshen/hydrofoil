package org.hydrofoil.provider.sequence;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.hydrofoil.common.provider.IDataConnectContext;
import org.hydrofoil.common.schema.PackageSchema;
import org.hydrofoil.common.schema.TableSchema;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.common.util.LangUtils;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.provider.sequence.loader.LocalFileLoader;
import org.hydrofoil.provider.sequence.reader.CsvFileReader;

import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
     * load data set,package and dataset
     * @param dataSourceContext datsource
     * @return package and dataset
     * @throws Exception
     */
    public static Map<String,Map<String,DataSet>> loadDataSet(IDataConnectContext dataSourceContext) throws Exception {
        final Map<String, String> configItems = dataSourceContext.
                getDatasourceSchema().getConfigItems();
        //create file loader
        String datasourceType = MapUtils.getString(configItems,SequenceConfiguration.DATASOURCE_TYPE);
        String directoryPath = MapUtils.getString(configItems,SequenceConfiguration.DATASOURCE_DIRECTORY_PATH);
        Class<? extends IFileLoader> loaderClz = loaderClassMap.get(datasourceType);
        ParameterUtils.notNull(loaderClz);
        IFileLoader loader = LangUtils.newInstance(loaderClz);
        ParameterUtils.notNull(loader);
        loader.create(dataSourceContext.getDatasourceSchema());
        //get file list
        Map<String,Map<String,DataSet>> packageMap = DataUtils.newHashMapWithExpectedSize(10);
        final Map<String, PackageSchema> packageSchemaMap = dataSourceContext.getPackageSchema();
        final Map<String, TableSchema> tableSchemaMap = dataSourceContext.getTableSchema();
        for(Map.Entry<String,PackageSchema> packageSchemaEntry:packageSchemaMap.entrySet()){
            String filePath = MapUtils.getString(packageSchemaEntry.getValue().getOptionMap(),SequenceConfiguration.FILE_PATH);
            String fileType = MapUtils.getString(packageSchemaEntry.getValue().getOptionMap(),SequenceConfiguration.FILE_TYPE);
            InputStream is = loader.load(directoryPath,filePath);
            final List<TableSchema> tableSchemas = tableSchemaMap.values().stream().
                    filter(p->StringUtils.equalsIgnoreCase(packageSchemaEntry.getKey(),p.getPackage())).collect(Collectors.toList());
            Map<String,FileTable> fileTables;
            try{
                IFileReader fileReader = readerMap.get(fileType);
                ParameterUtils.notNull(fileReader);
                fileTables = fileReader.read(is, packageSchemaEntry.getValue().getOptionMap(),tableSchemas);
            }finally {
                IOUtils.closeQuietly(is);
            }

            Map<String,DataSet> dataSetMap = packageMap.computeIfAbsent(packageSchemaEntry.getKey(),(k)->DataUtils.newMapWithMaxSize(5));
            fileTables.forEach((tableName,fileTable) -> {
                TableSchema tableSchema = (TableSchema) DataUtils.getOptional(tableSchemas.stream().
                        filter(p->StringUtils.equalsIgnoreCase(tableName,p.getRealName())).findFirst());
                DataSet dataSet = new DataSet(fileTable);
                //init table by schema
                dataSet.init(tableSchema);
                dataSetMap.put(fileTable.getTableName(),dataSet);
            });
        }
        return packageMap;
    }
}
