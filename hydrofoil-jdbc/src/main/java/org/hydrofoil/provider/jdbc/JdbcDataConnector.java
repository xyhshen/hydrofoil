package org.hydrofoil.provider.jdbc;

import org.apache.commons.collections4.MapUtils;
import org.apache.commons.dbcp2.BasicDataSource;
import org.hydrofoil.common.provider.IDataConnector;
import org.hydrofoil.common.provider.IDataConnectContext;
import org.hydrofoil.common.provider.datasource.*;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.provider.jdbc.internal.AbstractDbQueryService;
import org.hydrofoil.provider.jdbc.internal.MysqlDbQueryService;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * MysqlDataSource
 * <p>
 * package org.hydrofoil.provider.jdbc
 *
 * @author xie_yh
 * @date 2018/7/4 11:29
 */
public final class JdbcDataConnector implements IDataConnector {

    private BasicDataSource dataSource;
    private IDataConnectContext dataSourceContext;

    JdbcDataConnector(BasicDataSource dataSource,
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
    public Iterator<RowQueryResponse> performance(REQUEST_TYPE requestType, Collection<? extends BaseRowQuery> querySet, Map<String, Object> parameters) {
        final String groupField = MapUtils.getString(parameters,PARAMETER_GROUP_FIELD_NAME);
        return DataUtils.newIterator(querySet.iterator(), query->{
            try{
                AbstractDbQueryService queryServeice = null;//getQueryServeice(query);
                Collection<RowStore> rowStores = queryServeice.executeQuery();
                switch (requestType){
                    case GET:
                        return null;
                    case SCAN:
                        return null;
                    case COUNT:
                        return null;
                    default:break;
                }
            }catch(Throwable t){
                return RowQueryResponse.createFailedResponse(query.getId(),new SQLException(t));
            }
            return null;
        });
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
