package org.hydrofoil.provider.jdbc;

import org.apache.commons.dbcp2.BasicDataSource;
import org.hydrofoil.common.provider.IDataConnectContext;
import org.hydrofoil.common.schema.DataSourceSchema;
import org.hydrofoil.provider.jdbc.service.MysqlJdbcService;

import javax.sql.DataSource;
import java.io.Closeable;
import java.io.IOException;
import java.sql.SQLException;

import static org.hydrofoil.provider.jdbc.JdbcDatasourceSchema.DatasourceItem.*;

/**
 * JdbcDriver
 * <p>
 * package org.hydrofoil.provider.jdbc
 *
 * @author xie_yh
 * @date 2018/12/31 11:00
 */
public class JdbcConnection implements Closeable{

    private BasicDataSource dataSource;

    private final IDataConnectContext dataConnectContext;

    private JdbcDatasourceSchema.DataBaseType dbType;

    private IJdbcService jdbcService;

    private JdbcConnection(final IDataConnectContext dataConnectContext){
        this.dataConnectContext = dataConnectContext;
    }

    private void load(){
        createDatasource(dataConnectContext.getDatasourceSchema());
        if(dbType == JdbcDatasourceSchema.DataBaseType.Mysql){
            jdbcService = new MysqlJdbcService(dataSource,dataConnectContext);
        }
    }

    private void createDatasource(DataSourceSchema dataSourceSchema){
        //get data base type
        dbType = JdbcDatasourceSchema.DataBaseType.of(DbType.toString(dataSourceSchema.getConfigItems()));
        /*
         * create jdbc source
         */
        dataSource = new BasicDataSource();
        dataSource.setUrl(ConnectUrl.toString(dataSourceSchema.getConfigItems()));
        dataSource.setUsername(Username.toString(dataSourceSchema.getConfigItems()));
        dataSource.setPassword(Password.toString(dataSourceSchema.getConfigItems()));
        dataSource.setDriverClassName(dbType.getClassName());
        dataSource.setDefaultReadOnly(true);
        dataSource.setTestWhileIdle(TestWhileIdle.toBoolean(dataSourceSchema.getConfigItems()));
        dataSource.setMinIdle(ConnectPoolMinIdle.toInteger(dataSourceSchema.getConfigItems()));
        dataSource.setMaxIdle(ConnectPoolMaxIdle.toInteger(dataSourceSchema.getConfigItems()));
        dataSource.setMaxTotal(ConnectPoolMaxTotal.toInteger(dataSourceSchema.getConfigItems()));
        if(dbType == JdbcDatasourceSchema.DataBaseType.Mysql){
            dataSource.setValidationQuery("select 1 from dual");
        }
        dataSource.setValidationQueryTimeout(20);
    }

    public static JdbcConnection create(final IDataConnectContext dataConnectContext){
        JdbcConnection connection = new JdbcConnection(dataConnectContext);
        connection.load();
        return connection;
    }

    public IJdbcService getService(){
        return jdbcService;
    }

    /**
     * @return IDataConnectContext
     * @see JdbcConnection#dataConnectContext
     **/
    public IDataConnectContext getDataConnectContext() {
        return dataConnectContext;
    }

    /**
     * @return DataSource
     * @see JdbcConnection#dataSource
     **/
    public DataSource getDataSource() {
        return dataSource;
    }

    @Override
    public void close() throws IOException {
        try {
            dataSource.close();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
