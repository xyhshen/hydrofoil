package org.hydrofoil.provider.sequence.loader;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.StringUtils;
import org.hydrofoil.common.schema.DataSourceSchema;
import org.hydrofoil.common.util.ArgumentUtils;
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

    public static final String RESOURCE_PATH_PREFIX = "resource:";

    @Override
    public void create(DataSourceSchema dataSourceSchema) throws Exception {
        directoryPath = MapUtils.getString(dataSourceSchema.getConfigItems(),
                SequenceConfiguration.DATASOURCE_DIRECTORY_PATH);
        ArgumentUtils.notBlank(directoryPath);
    }

    @Override
    public InputStream load(final String directoryPath,final String path) throws IOException {
        String fullPath = FilenameUtils.concat(directoryPath,path);
        if(StringUtils.startsWith(fullPath,RESOURCE_PATH_PREFIX)){
            String resourcePath = StringUtils.removeStart(fullPath,RESOURCE_PATH_PREFIX);
            return Thread.currentThread().getContextClassLoader().getResourceAsStream(resourcePath);
        }
        return FileUtils.openInputStream(new File(fullPath));
    }

    @Override
    public void close() throws IOException {

    }
}
