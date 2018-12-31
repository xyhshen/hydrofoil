package org.hydrofoil.provider.jdbc;

import org.apache.commons.dbcp2.BasicDataSource;
import org.hydrofoil.common.provider.IDataProvider;
import org.hydrofoil.common.provider.IDataConnector;
import org.hydrofoil.common.provider.IDataConnectContext;
import org.hydrofoil.common.schema.DataSourceSchema;

import static org.hydrofoil.provider.jdbc.JdbcDatasourceSchema.DatasourceItem.*;

/**
 * DataProvider
 * <p>
 * package org.hydrofoil.provider.jdbc
 *
 * @author xie_yh
 * @date 2018/7/4 11:17
 */
public final class DataProvider implements IDataProvider {

    @Override
    public IDataConnector connect(IDataConnectContext dataSourceContext) {
        return new JdbcDataConnector(JdbcConnection.create(dataSourceContext));
    }
}
