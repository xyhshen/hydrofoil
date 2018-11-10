package org.hydrofoil.common.provider.datasource;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.hydrofoil.common.util.DataUtils;

import java.util.Collection;
import java.util.Map;

/**
 * RowColumnInformation
 * <p>
 * package org.hydrofoil.common.provider.datasource
 *
 * @author xie_yh
 * @date 2018/11/10 11:04
 */
public final class RowColumnInformation {

    private Map<String,Pair<Integer,Map<String,Integer>>> columnMapping;

    public RowColumnInformation(){
        columnMapping = DataUtils.newHashMapWithExpectedSize(0);
    }

    /**
     * define column
     * @param name data collect set
     * @param fieldname field name
     * @return this
     */
    public RowColumnInformation column(final String name,final String fieldname){
        Pair<Integer,Map<String,Integer>> indexPair = columnMapping.
                computeIfAbsent(name,k-> Pair.of(columnMapping.size(),
                        DataUtils.newHashMapWithExpectedSize(0)));
        Map<String,Integer> columnIndexMap = indexPair.getRight();
        if(columnIndexMap.containsKey(fieldname)){
            return this;
        }
        int index = columnIndexMap.size();
        columnIndexMap.put(fieldname,index);
        return this;
    }

    /**
     * get column index
     * @param name data collect set
     * @param fieldname field name
     * @return index
     */
    public Integer index(final String name,final String fieldname){
        final Pair<Integer,Map<String,Integer>> pair = columnMapping.get(name);
        if(pair == null){
            return null;
        }
        return MapUtils.getInteger(pair.getRight(),fieldname);
    }

    /**
     * get collect index
     * @param name collect name
     * @return index
     */
    public Integer index(final String name){
        final Pair<Integer,Map<String,Integer>> pair = columnMapping.get(name);
        if(pair == null){
            return null;
        }
        return pair.getLeft();
    }

    /**
     * total size
     * @return total size
     */
    int size(){
        return columnMapping.size();
    }

    /**
     * a collect size
     * @param name collect name
     * @return size
     */
    int size(final String name){
        final Pair<Integer,Map<String,Integer>> pair = columnMapping.get(name);
        if(pair == null){
            return 0;
        }
        return pair.getRight().size();
    }

    /**
     * get columns
     * @param name collect name
     * @return column's
     */
    public Collection<String> columns(final String name){
        final Pair<Integer, Map<String, Integer>> pair = columnMapping.get(name);
        if(pair == null){
            return null;
        }
        return pair.getRight().keySet();
    }

    /**
     * create a row store
     * @return row
     */
    public RowStore createRowStore(){
        return new RowStore(this);
    }

}
