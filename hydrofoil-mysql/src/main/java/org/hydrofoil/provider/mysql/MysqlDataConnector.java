package org.hydrofoil.provider.mysql;

import org.apache.commons.dbcp2.BasicDataSource;
import org.hydrofoil.common.provider.IDataConnector;
import org.hydrofoil.common.provider.IDataConnectContext;
import org.hydrofoil.common.provider.datasource.RowQueryGet;
import org.hydrofoil.common.provider.datasource.RowQueryScan;
import org.hydrofoil.common.provider.datasource.RowQueryResponse;
import org.hydrofoil.common.provider.datasource.RowStore;
import org.hydrofoil.provider.mysql.internal.AbstractDbQueryService;
import org.hydrofoil.provider.mysql.internal.MysqlDbQueryService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;

/**
 * MysqlDataSource
 * <p>
 * package org.hydrofoil.provider.mysql
 *
 * @author xie_yh
 * @date 2018/7/4 11:29
 */
public final class MysqlDataConnector implements IDataConnector {

    private BasicDataSource dataSource;
    private IDataConnectContext dataSourceContext;

    MysqlDataConnector(BasicDataSource dataSource,
                       IDataConnectContext dataSourceContext){
        this.dataSource = dataSource;
        this.dataSourceContext = dataSourceContext;
    }

    private AbstractDbQueryService getQueryServeice(RowQueryScan query){
        AbstractDbQueryService dbQueryService = new MysqlDbQueryService(dataSource,dataSourceContext,query);
        dbQueryService.init();
        return dbQueryService;
    }

    @Override
    public Iterator<RowQueryResponse> scanRow(final Collection<RowQueryScan> querySet) {
        return new Iterator<RowQueryResponse>() {

            private final Iterator<RowQueryScan> querySetIterator = querySet.iterator();

            @Override
            public boolean hasNext() {
                return querySetIterator.hasNext();
            }

            @Override
            public RowQueryResponse next() {
                RowQueryScan query = querySetIterator.next();
                AbstractDbQueryService queryServeice = getQueryServeice(query);
                RowQueryResponse response;
                try {
                    Collection<RowStore> rowStores = queryServeice.executeQuery();
                    response = new RowQueryResponse(true,rowStores);
                } catch (SQLException e) {
                    response = new RowQueryResponse(false);
                    response.setException(e);
                }
                response.setId(query.getId());
                return response;
            }
        };
    }

    @Override
    public Iterator<RowQueryResponse> getRows(Collection<RowQueryGet> querySet) {
        return null;
    }

    @Override
    public Long countRow(RowQueryScan query) {
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
