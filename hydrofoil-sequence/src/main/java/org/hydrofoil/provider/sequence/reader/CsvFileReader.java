package org.hydrofoil.provider.sequence.reader;

import com.csvreader.CsvReader;
import org.apache.commons.collections4.MapUtils;
import org.apache.commons.lang3.ArrayUtils;
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
    public List<FileTable> read(InputStream is, Map<String, String> optionMap) throws Exception {
        String encode = MapUtils.getString(optionMap, SequenceConfiguration.FILE_ENCODE,"UTF-8");
        String splitChar= MapUtils.getString(optionMap,SequenceConfiguration.FILE_CSV_SPLIT_CHARACTER,",");
        CsvReader reader = new CsvReader(is,splitChar.charAt(0), Charset.forName(encode));
        //read header
        reader.readRecord();
        String[] columns = reader.getValues();
        ParameterUtils.mustTrue(ArrayUtils.isNotEmpty(columns));
        FileTable fileTable = new FileTable("");
        for(int i = 0;i < columns.length;i++){
            fileTable.getHeader().put(columns[i],i);
        }

        //read file content
        while(reader.readRecord()){
            String[] values = reader.getValues();
            fileTable.putRow(values);
        }
        reader.close();
        return Collections.singletonList(fileTable);
    }
}
