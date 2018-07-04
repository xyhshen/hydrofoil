package org.hydrofoil.provider.mysql;

import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.dbutils.DbUtils;
import org.apache.commons.dbutils.QueryRunner;
import org.hydrofoil.common.provider.IDataSource;
import org.hydrofoil.common.provider.datasource.RowQueryRequest;
import org.hydrofoil.common.provider.datasource.RowQueryResponse;
import org.hydrofoil.common.provider.datasource.RowStore;

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

    /*private Collection<RowStore> select(RowQueryRequest query){
        StringBuffer sql = new StringBuffer();
        if(query.getAssociateQuery().isEmpty()){
            String.format("select %s from %s ",,query.getName());
        }
    }*/

    @Override
    public RowQueryResponse sendQuery(RowQueryRequest query) {
        return null;
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
