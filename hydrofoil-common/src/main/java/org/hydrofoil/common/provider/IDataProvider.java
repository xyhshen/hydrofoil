package org.hydrofoil.common.provider;

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
     * collect provider class path
     */
    public static final String DATA_PROVIDER_CLASS_PATH = "org.hydrofoil.provider";

    /**
     * collect provider class name
     */
    public static final String DATA_PROVIDER_CLASS_NAME = "DataProvider";

    /**
     * connect datasource
     * @param dataSourceContext datasource context
     * @return collect source's instance
     */
    IDataSource connect(IDataSourceContext dataSourceContext);

}
