package org.hydrofoil.provider.mysql;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.hydrofoil.common.provider.IDataSource;
import org.hydrofoil.common.provider.datasource.RowQueryRequest;
import org.hydrofoil.common.provider.datasource.RowQueryResponse;
import org.hydrofoil.common.provider.datasource.RowStore;
import org.hydrofoil.provider.mysql.internal.AbstractDbQueryService;
import org.hydrofoil.provider.mysql.internal.MysqlDbQueryService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

/**
 * MysqlDataSource
 * <p>
 * package org.hydrofoil.provider.mysql
 *
 * @author xie_yh
 * @date 2018/7/4 11:29
 */
public final class MysqlDataSource implements IDataSource {

    private BasicDataSource dataSource;

    MysqlDataSource(BasicDataSource dataSource){
        this.dataSource = dataSource;
    }

    private AbstractDbQueryService getQueryServeice(RowQueryRequest query){
        AbstractDbQueryService dbQueryService = new MysqlDbQueryService(dataSource,query);
        dbQueryService.init();
        return dbQueryService;
    }

    @Override
    public RowQueryResponse sendQuery(RowQueryRequest query) {
        AbstractDbQueryService queryServeice = getQueryServeice(query);
        RowQueryResponse response;
        try {
            Collection<RowStore> rowStores = queryServeice.executeQuery();
            response = new RowQueryResponse(true,rowStores);
        } catch (SQLException e) {
            response = new RowQueryResponse(false);
            e.printStackTrace();
        }
        return response;
    }

    @Override
    public Long count(RowQueryRequest query) {
        return null;
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
