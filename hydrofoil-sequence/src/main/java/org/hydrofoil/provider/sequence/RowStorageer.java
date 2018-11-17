package org.hydrofoil.provider.sequence;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.hydrofoil.common.graph.QMatch;
import org.hydrofoil.common.provider.IDataConnectContext;
import org.hydrofoil.common.provider.datasource.*;
import org.hydrofoil.common.schema.TableSchema;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.common.util.ParameterUtils;

import java.util.*;
import java.util.stream.Collectors;

/**
 * RowStorageer
 * <p>
 * package org.hydrofoil.provider.sequence
 *
 * @author xie_yh
 * @date 2018/11/8 19:23
 */
public final class RowStorageer {

    private final IDataConnectContext dataSourceContext;

    private Map<String,Map<String,DataSet>> namespaceDataSetMap;

    RowStorageer(IDataConnectContext dataSourceContext){
        this.dataSourceContext = dataSourceContext;
    }

    void load() throws Exception {
        namespaceDataSetMap = Loaders.loadDataSet(dataSourceContext);
    }

    private DataSet getDataSet(String tableName){
        final Map<String, TableSchema> tableSchemaMap = dataSourceContext.getTableSchema(tableName);
        final TableSchema tableSchema = tableSchemaMap.get(tableName);
        ParameterUtils.notNull(tableSchema);
        final Map<String, DataSet> dataSetMap = namespaceDataSetMap.
                get(tableSchema.getNamespace());
        return dataSetMap.get(tableSchema.getRealName());
    }

    RowQueryResponse scanRows(final RowQueryScan rowQueryScan){
        final DataSet dataSet = getDataSet(rowQueryScan.getName());
        final List<FileRow> crossRows = dataSet.selectWhere(rowQueryScan.getMatch().stream().map((q) ->
                Pair.of(q, "")).collect(Collectors.toList()));
        List<RowStore> rowStores = crossRows.stream().map(crossRow -> {
            final Map<String, Collection<FileRow>> joinRowMap = joinOnAll(crossRow, rowQueryScan,true);
            if(joinRowMap == null){
                return null;
            }
            return combineRowKeyValue(crossRow, joinRowMap, rowQueryScan);
        }).filter(Objects::nonNull).collect(Collectors.toList());
        rowStores = DataUtils.ranger(rowStores,rowQueryScan.getOffset(),rowQueryScan.getLimit());
        return rowQueryScan.createResponse(rowStores);
    }

    RowQueryResponse getRows(final RowQueryGet rowQueryGet){
        final Collection<RowKey> rowKeys = rowQueryGet.getRowKeys();
        final DataSet dataSet = getDataSet(rowQueryGet.getName());
        final List<FileRow> crossRows = dataSet.selectIn(rowKeys);
        final List<RowStore> rowStores = crossRows.stream().map(crossRow -> {
            final Map<String, Collection<FileRow>> joinRowMap = joinOnAll(crossRow, rowQueryGet,false);
            return combineRowKeyValue(crossRow, joinRowMap, rowQueryGet);
        }).collect(Collectors.toList());
        return rowQueryGet.createResponse(rowStores);
    }

    RowQueryResponse countRow(final RowQueryCount rowQueryCount){
        final DataSet dataSet = getDataSet(rowQueryCount.getName());
        final List<FileRow> crossRows = dataSet.selectWhere(rowQueryCount.getMatch().stream().map((q) ->
                Pair.of(q, "")).collect(Collectors.toList()));
        Integer count = crossRows.stream().map(crossRow -> {
            final Map<String, Collection<FileRow>> joinRowMap = joinOnAll(crossRow, rowQueryCount,true);
            if(joinRowMap == null){
                return 0;
            }
            return 1;
        }).reduce((v1,v2)->v1+v2).orElse(0);
        return rowQueryCount.createResponse(count);
    }

    private RowStore combineRowKeyValue(FileRow crossRow,Map<String,Collection<FileRow>> joinRowMap,BaseRowQuery baseRowQuery){
        final RowColumnInformation information = baseRowQuery.getColumnInformation();
        RowStore rowStore = information.createRowStore();
        information.columns(baseRowQuery.getName()).forEach(fieldName->{
            rowStore.put(baseRowQuery.getName(),fieldName,crossRow.value(fieldName));
        });
        baseRowQuery.getAssociateQuery().forEach(associateRowQuery -> {
            if(associateRowQuery.isOnlyQueries()){
                return;
            }
            final Collection<FileRow> fileRows = joinRowMap.get(associateRowQuery.getName());
            Collection<String> fields = information.columns(associateRowQuery.getName());
            if(CollectionUtils.isEmpty(fileRows)){
                return;
            }
            if(associateRowQuery.isOneToMany()){
                //write all row
                fileRows.forEach(fileRow -> {
                    writeRowStore(information,associateRowQuery.getName(),rowStore,fileRow);
                });
            }else{
                final FileRow fileRow = DataUtils.collectFirst(fileRows);
                //write a single row
                writeRowStore(information,associateRowQuery.getName(),rowStore,fileRow);
            }
        });
        return rowStore;
    }

    private void writeRowStore(RowColumnInformation information,String name,RowStore rowStore,FileRow fileRow){
        information.columns(name).forEach(fieldName->{
            rowStore.put(name,fieldName,fileRow.value(fieldName));
        });
    }

    private Map<String,Collection<FileRow>> joinOnAll(FileRow crossRow, BaseRowQuery baseRowQuery,boolean mustNotEmpty){
        Map<String,Collection<FileRow>> fileRowMap = DataUtils.newMapWithMaxSize(0);
        for(BaseRowQuery.AssociateRowQuery associateRowQuery:baseRowQuery.getAssociateQuery()){
            final Collection<FileRow> fileRows = joinOn(crossRow, associateRowQuery.getName(), associateRowQuery.getMatch());
            if(associateRowQuery.hasNoneQuery() && CollectionUtils.isEmpty(fileRows) && mustNotEmpty){
                return null;
            }
            fileRowMap.put(associateRowQuery.getName(),fileRows);
        }
        return fileRowMap;
    }

    private Collection<FileRow> joinOn(FileRow crossRow,String tableName,Set<BaseRowQuery.AssociateMatch> associateMatches){
        List<Pair<QMatch.Q,String>> pl = associateMatches.stream().map(associateMatch -> {
            Pair<QMatch.Q,String> p;
            if(StringUtils.isBlank(associateMatch.getJoinField())){
                p = Pair.of(associateMatch.getMatch(),null);
            }else{
                p = Pair.of(associateMatch.getMatch(),crossRow.value(associateMatch.getJoinField()));
            }
            return p;
        }).collect(Collectors.toList());
        final DataSet dataSet = getDataSet(tableName);
        return dataSet.selectWhere(pl);
    }
}
