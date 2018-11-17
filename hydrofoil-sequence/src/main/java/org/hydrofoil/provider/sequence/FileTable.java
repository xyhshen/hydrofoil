package org.hydrofoil.provider.sequence;

import org.hydrofoil.common.util.DataUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * FileTable
 * <p>
 * package org.hydrofoil.provider.sequence
 *
 * @author xie_yh
 * @date 2018/10/15 19:24
 */
public final class FileTable {

    /**
     * table name
     */
    private String tableName;

    /**
     * table header
     */
    private Map<String,Integer> header;

    /**
     * row's
     */
    private List<FileRow> rows;

    public FileTable(String tableName){
        this(tableName,20);
    }

    public FileTable(String tableName,int rowCount){
        this.tableName = tableName;
        this.header = DataUtils.newMapWithMaxSize(10);
        this.rows = new ArrayList<>(rowCount);
    }


    /**
     * @return String
     * @see FileTable#tableName
     **/
    public String getTableName() {
        return tableName;
    }

    /**
     * @return Integer>
     * @see FileTable#header
     **/
    public Map<String, Integer> getHeader() {
        return header;
    }

    /**
     * put a row
     * @param row row
     */
    public void putRow(String[] row){
        rows.add(new FileRow(row,header));
    }

    /**
     * get row by line row
     * @param line line number
     * @return result
     */
    public FileRow getRow(int line){
        return DataUtils.lookup(rows,line);
    }

    /**
     * get all rows
     * @return rows
     */
    public List<FileRow> getRows(){
        return rows;
    }

}
