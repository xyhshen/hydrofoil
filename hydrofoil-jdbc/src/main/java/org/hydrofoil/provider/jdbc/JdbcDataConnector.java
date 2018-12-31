package org.hydrofoil.provider.jdbc;

import org.apache.commons.collections4.MapUtils;
import org.hydrofoil.common.provider.IDataConnector;
import org.hydrofoil.common.provider.datasource.BaseRowQuery;
import org.hydrofoil.common.provider.datasource.RowQueryGet;
import org.hydrofoil.common.provider.datasource.RowQueryResponse;
import org.hydrofoil.common.provider.datasource.RowQueryScan;
import org.hydrofoil.common.util.DataUtils;

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

    private JdbcConnection connection;

    JdbcDataConnector(final JdbcConnection connection){
        this.connection = connection;
    }

    @Override
    public Iterator<RowQueryResponse> performance(REQUEST_TYPE requestType, Collection<? extends BaseRowQuery> querySet, Map<String, Object> parameters) {
        final String groupField = MapUtils.getString(parameters,PARAMETER_GROUP_FIELD_NAME);
        return DataUtils.newIterator(querySet.iterator(), query->{
            try{
                final IJdbcService service = connection.getService();
                switch (requestType){
                    case GET:
                        return service.scanRows((RowQueryScan) query);
                    case SCAN:
                        return service.getRows((RowQueryGet) query);
                    case COUNT:
                        return service.countRow(query,groupField);
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
        connection.close();
    }
}
