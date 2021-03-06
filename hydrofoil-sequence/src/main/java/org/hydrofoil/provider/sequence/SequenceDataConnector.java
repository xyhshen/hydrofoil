package org.hydrofoil.provider.sequence;

import org.apache.commons.collections4.MapUtils;
import org.hydrofoil.common.provider.IDataConnectContext;
import org.hydrofoil.common.provider.IDataConnector;
import org.hydrofoil.common.provider.datasource.*;
import org.hydrofoil.common.util.DataUtils;

import java.sql.SQLException;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

/**
 * SequenceDataSource
 * <p>
 * package org.hydrofoil.provider.sequence
 *
 * @author xie_yh
 * @date 2018/10/15 18:57
 */
public class SequenceDataConnector implements IDataConnector {

    private final IDataConnectContext dataSourceContext;

    private RowStorageer rowStorageer;

    SequenceDataConnector(final IDataConnectContext dataSourceContext){
        this.dataSourceContext = dataSourceContext;
    }

    void init() throws Exception{
        rowStorageer = new RowStorageer(dataSourceContext);
        rowStorageer.load();
    }

    @Override
    public Iterator<RowQueryResponse> performance(REQUEST_TYPE requestType, Collection<? extends BaseRowQuery> querySet,final Map<String,Object> parameters) {
        final String groupField = MapUtils.getString(parameters,PARAMETER_GROUP_FIELD_NAME);
        return DataUtils.newIterator(querySet.iterator(),query->{
            try{
                switch (requestType){
                    case GET:
                        return rowStorageer.getRows((RowQueryGet) query);
                    case SCAN:
                        return rowStorageer.scanRows((RowQueryScan) query);
                    case COUNT:
                        return rowStorageer.countRow(query, groupField);
                    default:break;
                }
            }catch(Throwable t){
                return RowQueryResponse.createFailedResponse(query.getId(),new SQLException(t));
            }
            return null;
        });
    }

    @Override
    public void close() {

    }
}
