package org.hydrofoil.provider.sequence.loader;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.hydrofoil.common.schema.DataSourceSchema;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.provider.sequence.IFileLoader;
import org.hydrofoil.provider.sequence.SequenceConfiguration;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

/**
 * LocalFileLoader
 * <p>
 * package org.hydrofoil.provider.sequence.loader
 *
 * @author xie_yh
 * @date 2018/10/16 9:48
 */
public class LocalFileLoader implements IFileLoader {

    private String directoryPath;

    @Override
    public void create(DataSourceSchema dataSourceSchema) throws Exception {
        directoryPath = MapUtils.getString(dataSourceSchema.getConfigItems(),
                SequenceConfiguration.DATASOURCE_DIRECTORY_PATH);
        ParameterUtils.notBlank(directoryPath);
    }

    @Override
    public InputStream load(String path) throws IOException {
        return FileUtils.openInputStream(new File(path));
    }

    @Override
    public void close() throws IOException {

    }
}
