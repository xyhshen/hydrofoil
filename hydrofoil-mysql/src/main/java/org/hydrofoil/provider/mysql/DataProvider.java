package org.hydrofoil.provider.mysql;

import org.apache.commons.dbcp2.BasicDataSource;
import org.hydrofoil.common.provider.IDataProvider;
import org.hydrofoil.common.provider.IDataSource;
import org.hydrofoil.common.schema.DataSourceSchema;

import static org.hydrofoil.provider.mysql.MysqlDatasourceSchema.DatasourceItem.*;

/**
 * DataProvider
 * <p>
 * package org.hydrofoil.provider.mysql
 *
 * @author xie_yh
 * @date 2018/7/4 11:17
 */
public final class DataProvider implements IDataProvider {

    @Override
    public IDataSource connect(DataSourceSchema dataSourceSchema) {
        BasicDataSource datasource = createDatasource(dataSourceSchema);
        return new MysqlDataSource(datasource);
    }

    private BasicDataSource createDatasource(DataSourceSchema dataSourceSchema){
        /*
         * create jdbc source
         */
        BasicDataSource dataSource = new BasicDataSource();
        dataSource.setUrl(ConnectUrl.toString(dataSourceSchema.getConfigItems()));
        dataSource.setUsername(Username.toString(dataSourceSchema.getConfigItems()));
        dataSource.setPassword(Password.toString(dataSourceSchema.getConfigItems()));
        dataSource.setDriverClassName(MysqlDatasourceSchema.MYSQL_DRIVER_NAME);
        dataSource.setDefaultReadOnly(true);
        dataSource.setTestWhileIdle(TestWhileIdle.toBoolean(dataSourceSchema.getConfigItems()));
        dataSource.setValidationQuery(ValidationQuery.toString(dataSourceSchema.getConfigItems()));
        dataSource.setValidationQueryTimeout(ValidationQueryTimeout.toInteger(dataSourceSchema.getConfigItems()));
        dataSource.setMinIdle(2);
        dataSource.setMaxIdle(5);
        dataSource.setMaxTotal(8);
        return dataSource;
    }
}
