package org.hydrofoil.provider.sequence.reader;

import com.csvreader.CsvReader;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.hydrofoil.common.schema.TableSchema;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.provider.sequence.FileTable;
import org.hydrofoil.provider.sequence.IFileReader;
import org.hydrofoil.provider.sequence.SequenceConfiguration;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * CsvFileReader
 * <p>
 * package org.hydrofoil.provider.sequence.reader
 *
 * @author xie_yh
 * @date 2018/10/16 16:42
 */
public class CsvFileReader implements IFileReader {

    @Override
    public Map<String,FileTable> read(InputStream is, Map<String, String> optionMap,List<TableSchema> tableSchemas) throws Exception {
        String encode = MapUtils.getString(optionMap, SequenceConfiguration.FILE_ENCODE,"UTF-8");
        String delimiter= MapUtils.getString(optionMap,SequenceConfiguration.FILE_CSV_DELIMITER,",");
        CsvReader reader = new CsvReader(is,delimiter.charAt(0), Charset.forName(encode));

        ParameterUtils.notEmpty(tableSchemas);

        //read header
        reader.readRecord();
        String[] columns = reader.getValues();
        ParameterUtils.mustTrue(ArrayUtils.isNotEmpty(columns));
        final TableSchema tableSchema = DataUtils.lookup(tableSchemas, 0);
        String tableName = tableSchema.getRealName();
        FileTable fileTable = new FileTable(tableSchema);
        for(int i = 0;i < columns.length;i++){
            fileTable.getHeader().put(columns[i],i);
        }

        //read file content
        while(reader.readRecord()){
            String[] values = reader.getValues();
            fileTable.putRow(values);
        }
        reader.close();
        return Collections.singletonMap(tableName,fileTable);
    }
}
