package org.hydrofoil.provider.sequence;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.ListUtils;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.collections4.SetValuedMap;
import org.apache.commons.collections4.multimap.HashSetValuedHashMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.hydrofoil.common.graph.QMatch;
import org.hydrofoil.common.provider.datasource.RowKey;
import org.hydrofoil.common.schema.TableSchema;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.common.util.EncodeUtils;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.common.util.bean.FieldTriple;
import org.hydrofoil.common.util.collect.SearchArrayList;

import java.util.*;
import java.util.stream.Collectors;

/**
 * DataSet
 * <p>
 * package org.hydrofoil.provider.sequence
 *
 * @author xie_yh
 * @date 2018/10/16 18:07
 */
public final class DataSet {

    /**
     * file table
     */
    private final FileTable fileTable;

    /**
     * indxe map
     */
    private final Map<String,SearchArrayList<String,Integer>> indexMap;

    private final Map<String,SetValuedMap<String,Integer>> textIndexMap;

    private final Map<String,Integer> rowKeyMap;

    DataSet(final FileTable fileTable){
        this.fileTable = fileTable;
        this.indexMap = DataUtils.newMapWithMaxSize(5);
        this.textIndexMap = DataUtils.newMapWithMaxSize(5);
        this.rowKeyMap = DataUtils.newHashMapWithExpectedSize(100);
    }

    void init(TableSchema tableSchema){
        List<String> primaryKeys = new ArrayList<>();
        //init index
        tableSchema.getColumns().forEach((columnName, columnSchema) -> {
            if(columnSchema.canPrimaryQuery()){
                primaryKeys.add(columnName);
            }
            if(!columnSchema.canNormalQuery() &&
                    !columnSchema.canFullTextQuery()){
                return;
            }
            if(columnSchema.canNormalQuery()){
                SearchArrayList<String,Integer> index = new SearchArrayList<>();
                int columnSeq = fileTable.getHeader().get(columnName);
                for(int i = 0;i < fileTable.getRows().size();i++){
                    index.add(fileTable.getRows().get(i).values()[columnSeq],i);
                }
                index.sorting();
                indexMap.put(columnName,index);
            }
            if(columnSchema.canFullTextQuery()){
                int columnSeq = fileTable.getHeader().get(columnName);
                SetValuedMap<String,Integer> index = new HashSetValuedHashMap<>(100);
                for(int i = 0;i < fileTable.getRows().size();i++){
                    String value = fileTable.getRows().get(i).values()[columnSeq];
                    final Set<String> wordSet = EncodeUtils.splitText(value);
                    final int indexId = i;
                    wordSet.forEach(word->{
                        index.put(word,indexId);
                    });
                }
                textIndexMap.put(columnName,index);
            }
        });

        ParameterUtils.notEmpty(primaryKeys);
        Collections.sort(primaryKeys);
        List<Integer> primaryKeyIndex = new ArrayList<>(primaryKeys.size());
        final Map<String, Integer> header = fileTable.getHeader();
        primaryKeys.forEach(primaryKey->{
            primaryKeyIndex.add(MapUtils.getInteger(header,primaryKey));
        });
        for(int i = 0;i < fileTable.getRows().size();i++){
            final FileRow row = fileTable.getRow(i);
            final StringBuilder rowKey = new StringBuilder();
            primaryKeyIndex.forEach(location->{
                rowKey.append(row.values()[location]);
            });
            rowKeyMap.put(rowKey.toString(),i);
        }
    }

    List<FileRow> selectWhere(final List<Pair<QMatch.Q,String>> matches){
        List<Integer> l = new ArrayList<>();
        if(CollectionUtils.isEmpty(matches)){
            return fileTable.getRows();
        }
        for(Pair<QMatch.Q,String> match:matches){
            QMatch.Q q = match.getLeft();
            SearchArrayList<String, Integer> index = indexMap.get(q.pair().name());
            final SetValuedMap<String, Integer> textIndex = textIndexMap.get(q.pair().name());
            List<Integer> rowNumbers = new ArrayList<>();
            String firstValue = StringUtils.isNotBlank(match.getRight())?match.getRight():Objects.toString(q.pair().first(), null);
            if(q.type() != QMatch.QType.like){
                ParameterUtils.mustTrue(index != null);
                if(q.type() == QMatch.QType.eq){
                    rowNumbers.addAll(index.find(firstValue));
                }else if(q.type() == QMatch.QType.prefix){
                    rowNumbers.addAll(CollectionUtils.emptyIfNull(index.getRange(firstValue,firstValue + Character.toString((char) 255))));
                }else if(q.type() == QMatch.QType.between){
                    String end = Objects.toString(((FieldTriple)q.pair()).last(), null);
                    rowNumbers.addAll(CollectionUtils.emptyIfNull(index.getRange(firstValue,end)));
                }
            }else{
                ParameterUtils.mustTrue(textIndex != null);
                rowNumbers.addAll(CollectionUtils.emptyIfNull(textIndex.get(firstValue)));
            }
            if(rowNumbers.isEmpty()){
                break;
            }
            l = l.isEmpty()?rowNumbers:ListUtils.intersection(l,rowNumbers);
        }
        return l.stream().map(fileTable::getRow).
                collect(Collectors.toList());
    }

    private String makeRowKey(Map<String,Object> primaryKey){
        List<String> pl = new ArrayList<>(primaryKey.keySet());
        Collections.sort(pl);
        return pl.stream().map(p->MapUtils.getString(primaryKey,p)).reduce((r1,r2)-> r1 + r2).orElse(null);
    }

    List<FileRow> selectIn(final Collection<RowKey> primaryKeys){
        return primaryKeys.stream().map(primaryKey->{
            String rowKey = makeRowKey(primaryKey.getKeyValueMap());
            Integer i = MapUtils.getInteger(rowKeyMap,rowKey);
            return fileTable.getRow(i);
        }).collect(Collectors.toList());
    }
}