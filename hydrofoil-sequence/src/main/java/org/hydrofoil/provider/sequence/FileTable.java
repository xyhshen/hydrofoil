package org.hydrofoil.provider.sequence;

import org.hydrofoil.common.schema.ColumnSchema;
import org.hydrofoil.common.schema.TableSchema;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.common.util.SqlUtils;

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
    private TableSchema tableSchema;

    /**
     * table header
     */
    private Map<String,Integer> header;

    /**
     * row's
     */
    private List<FileRow> rows;

    public FileTable(TableSchema tableSchema){
        this(tableSchema,20);
    }

    public FileTable(TableSchema tableSchema,int rowCount){
        this.tableSchema = tableSchema;
        this.header = DataUtils.newMapWithMaxSize(10);
        this.rows = new ArrayList<>(rowCount);
    }


    /**
     * @return String
     * @see FileTable#tableSchema
     **/
    public String getTableName() {
        return tableSchema.getRealName();
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
    public void putRow(Object[] row){
        final Map<String, ColumnSchema> columnMap = tableSchema.getColumns();
        columnMap.values().forEach(columnSchema -> {
            Integer i = header.get(columnSchema.getColumnName());
            try {
                row[i] = SqlUtils.rawDataToAcceptData(columnSchema,row[i]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
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
