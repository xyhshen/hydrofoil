package org.hydrofoil.common.provider;

import org.hydrofoil.common.schema.DataSourceSchema;

/**
 * IDataProvider
 * <p>
 * package org.hydrofoil.common.provider
 *
 * @author xie_yh
 * @date 2018/6/28 17:11
 */
public interface IDataProvider {

    /**
     * data provider class path
     */
    public static final String DATA_PROVIDER_CLASS_PATH = "org.hydrofoil.provider";

    /**
     * data provider class name
     */
    public static final String DATA_PROVIDER_CLASS_NAME = "DataProvider";

    /**
     * connect datasource
     * @param dataSourceSchema datasource schema
     * @return data source's instance
     */
    IDataSource connect(DataSourceSchema dataSourceSchema);

}
