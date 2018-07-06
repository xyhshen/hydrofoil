package org.hydrofoil.core.management;

import org.hydrofoil.common.provider.IDataSource;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * DataProviderManager
 * <p>
 * package org.hydrofoil.core.management
 *
 * @author xie_yh
 * @date 2018/7/2 16:06
 */
public final class DataSourceManager implements Closeable {

    private Map<String,IDataSource> dataSourceMap;

    public DataSourceManager(){
        this.dataSourceMap = new HashMap<>();
    }

    public IDataSource getDatasource(){
        return null;
    }

    @Override
    public void close() throws IOException {

    }
}
