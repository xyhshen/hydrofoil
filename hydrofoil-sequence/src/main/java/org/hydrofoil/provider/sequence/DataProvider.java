package org.hydrofoil.provider.sequence;

import org.hydrofoil.common.provider.IDataProvider;
import org.hydrofoil.common.provider.IDataConnector;
import org.hydrofoil.common.provider.IDataConnectContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * DataProvider
 * <p>
 * package org.hydrofoil.provider.sequence
 *
 * @author xie_yh
 * @date 2018/10/15 18:47
 */
public final class DataProvider implements IDataProvider {

    private static Logger logger = LoggerFactory.getLogger(DataProvider.class);

    @Override
    public IDataConnector connect(IDataConnectContext dataSourceContext) {
        final SequenceDataConnector sequenceDataSource = new SequenceDataConnector(dataSourceContext);
        try {
            sequenceDataSource.init();
        } catch (Throwable t) {
            logger.error("init data source failed!",t);
            return null;
        }
        return sequenceDataSource;
    }
}
