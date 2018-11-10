package org.hydrofoil.provider.sequence;

import org.hydrofoil.common.schema.DataSourceSchema;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;

/**
 * FileLoader
 * <p>
 * package org.hydrofoil.provider.sequence
 *
 * @author xie_yh
 * @date 2018/10/15 19:17
 */
public interface IFileLoader extends Closeable{

    /**
     * create file loader
     * @param dataSourceSchema data source schema
     * @throws Exception
     */
    void create(DataSourceSchema dataSourceSchema) throws Exception;

    /**
     * load file
     * @param path file path
     * @return result
     * @throws IOException
     */
    InputStream load(String path) throws IOException;

}
