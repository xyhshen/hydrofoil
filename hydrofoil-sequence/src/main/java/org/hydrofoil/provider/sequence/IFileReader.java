package org.hydrofoil.provider.sequence;

import org.hydrofoil.common.schema.TableSchema;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

/**
 * IFileReader
 * <p>
 * package org.hydrofoil.provider.sequence
 *
 * @author xie_yh
 * @date 2018/10/15 19:21
 */
public interface IFileReader {

    /**
     * read all file table
     * @param is file input stream
     * @return table's
     */
    Map<String,FileTable> read(InputStream is, Map<String,String> optionMap,List<TableSchema> tableSchemas) throws Exception;

}
