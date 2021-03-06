package org.hydrofoil.provider.sequence;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.hydrofoil.common.graph.QMatch;
import org.hydrofoil.common.provider.IDataConnectContext;
import org.hydrofoil.common.provider.datasource.*;
import org.hydrofoil.common.schema.TableSchema;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.common.util.ArgumentUtils;
import org.hydrofoil.common.util.bean.KeyValueEntity;

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

    private Map<String,Map<String,DataSet>> packageDataSetMap;

    RowStorageer(IDataConnectContext dataSourceContext){
        this.dataSourceContext = dataSourceContext;
    }

    void load() throws Exception {
        packageDataSetMap = Loaders.loadDataSet(dataSourceContext);
    }

    private DataSet getDataSet(String tableName){
        final Map<String, TableSchema> tableSchemaMap = dataSourceContext.getTableSchema(tableName);
        final TableSchema tableSchema = tableSchemaMap.get(tableName);
        ArgumentUtils.notNull(tableSchema);
        final Map<String, DataSet> dataSetMap = packageDataSetMap.
                get(tableSchema.getPackage());
        return dataSetMap.get(tableSchema.getRealName());
    }

    RowQueryResponse scanRows(final RowQueryScan rowQueryScan){
        final DataSet dataSet = getDataSet(rowQueryScan.getName());
        final List<FileRow> crossRows = dataSet.selectWhere(rowQueryScan.getMatch().stream().map((q) ->
                Pair.of(q, null)).collect(Collectors.toList()));
        final RowQueryScanKey scanKey = rowQueryScan.getScanKey();
        List<RowStore> rowStores = crossRows.stream().map(crossRow -> {
            Map<String, Collection<FileRow>> joinRowMap = joinOnAll(crossRow, rowQueryScan,true);
            if(joinRowMap == null){
                return null;
            }
            return combineRowKeyValue(crossRow, joinRowMap, rowQueryScan,scanKey != null?scanKey.getName():null);
        }).filter(Objects::nonNull).collect(Collectors.toList());
        if(scanKey != null){
            rowStores = (List<RowStore>) scanKey.filter(rowStores);
        }
        rowStores = DataUtils.range(rowStores,rowQueryScan.getOffset(),rowQueryScan.getLimit());
        return rowQueryScan.createResponse(rowStores);
    }

    RowQueryResponse getRows(final RowQueryGet rowQueryGet){
        final Collection<KeyValueEntity> rowKeys = rowQueryGet.getRowKeys();
        final DataSet dataSet = getDataSet(rowQueryGet.getName());
        final List<FileRow> crossRows = dataSet.selectIn(rowKeys);
        final List<RowStore> rowStores = crossRows.stream().map(crossRow -> {
            final Map<String, Collection<FileRow>> joinRowMap = joinOnAll(crossRow, rowQueryGet,false);
            return combineRowKeyValue(crossRow, joinRowMap, rowQueryGet,null);
        }).collect(Collectors.toList());
        return rowQueryGet.createResponse(rowStores);
    }

    RowQueryResponse countRow(final BaseRowQuery baseRowQuery,String groupField){
        final DataSet dataSet = getDataSet(baseRowQuery.getName());
        Long count;
        if(baseRowQuery instanceof RowQueryGet){
            RowQueryGet rowQueryGet = (RowQueryGet) baseRowQuery;
            final Collection<KeyValueEntity> rowKeys = rowQueryGet.getRowKeys();
            final List<FileRow> crossRows = dataSet.selectIn(rowKeys);
            count = (long)crossRows.size();
        }else{
            RowQueryScan rowQueryScan = (RowQueryScan) baseRowQuery;
            final List<FileRow> crossRows = dataSet.selectWhere(rowQueryScan.getMatch().stream().map((q) ->
                    Pair.of(q, null)).collect(Collectors.toList()));
            count = crossRows.stream().map(crossRow -> {
                final Map<String, Collection<FileRow>> joinRowMap = joinOnAll(crossRow, rowQueryScan,true);
                if(joinRowMap == null){
                    return 0L;
                }
                return 1L;
            }).reduce((v1,v2)->v1+v2).orElse(0L);
        }
        return baseRowQuery.createCountResponse(count);
    }

    private RowStore combineRowKeyValue(FileRow crossRow,Map<String,Collection<FileRow>> joinRowMap,BaseRowQuery baseRowQuery,String keyTableName){
        final RowColumnInformation information = baseRowQuery.getColumnInformation();
        RowStore rowStore = information.createRowStore();
        information.columns(baseRowQuery.getName()).forEach(fieldName->{
            rowStore.put(baseRowQuery.getName(),fieldName,crossRow.value(fieldName));
        });
        baseRowQuery.getAssociateQuery().forEach(associateRowQuery -> {
            if(associateRowQuery.isOnlyQueries()){
                return;
            }
            if(StringUtils.isNotBlank(keyTableName) &&
                    !StringUtils.equalsIgnoreCase(keyTableName,associateRowQuery.getName())){
                return;
            }
            final Collection<FileRow> fileRows = joinRowMap.get(associateRowQuery.getName());
            if(CollectionUtils.isEmpty(fileRows)){
                return;
            }
            if(associateRowQuery.isOneToMany()){
                //write all row
                fileRows.forEach(fileRow -> {
                    writeRowStore(information,associateRowQuery.getName(),rowStore.createRow(associateRowQuery.getName()),fileRow,true);
                });
            }else{
                final FileRow fileRow = DataUtils.collectFirst(fileRows);
                //write a single row
                writeRowStore(information,associateRowQuery.getName(),rowStore,fileRow,false);
            }
        });
        return rowStore;
    }

    private void writeRowStore(RowColumnInformation information,String name,RowStore rowStore,FileRow fileRow,boolean small){
        information.columns(name).forEach(fieldName->{
            if(small){
                rowStore.put(fieldName,fileRow.value(fieldName));
            }else{
                rowStore.put(name,fieldName,fileRow.value(fieldName));
            }
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
        List<Pair<QMatch.Q,Object>> pl = associateMatches.stream().map(associateMatch -> {
            Pair<QMatch.Q,Object> p;
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
