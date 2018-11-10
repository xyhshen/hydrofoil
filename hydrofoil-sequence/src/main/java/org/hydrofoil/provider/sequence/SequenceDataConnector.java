package org.hydrofoil.provider.sequence;

import org.hydrofoil.common.provider.IDataConnectContext;
import org.hydrofoil.common.provider.IDataConnector;
import org.hydrofoil.common.provider.datasource.*;
import org.hydrofoil.common.util.DataUtils;

import java.util.Collection;
import java.util.Iterator;

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
    public Iterator<RowQueryResponse> scanRow(Collection<RowQueryScan> querySet) {
        return DataUtils.newIterator(querySet.iterator(),query-> rowStorageer.scanRows(query));
    }

    @Override
    public Iterator<RowQueryResponse> getRows(Collection<RowQueryGet> querySet) {
        return DataUtils.newIterator(querySet.iterator(),query-> rowStorageer.getRows(query));
    }

    @Override
    public RowQueryCountResponse countRow(RowQueryCount query) {
        return null;
    }

    @Override
    public void close() {

    }
}
