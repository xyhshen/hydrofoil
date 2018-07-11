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
     * 链接数据源
     * @param dataSourceSchema 数据源schema
     * @return 数据源实例
     */
    IDataSource connect(DataSourceSchema dataSourceSchema);

}
