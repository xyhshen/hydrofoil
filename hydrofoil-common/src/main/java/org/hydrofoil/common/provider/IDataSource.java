package org.hydrofoil.common.provider;

import org.hydrofoil.common.provider.datasource.RowQueryRequest;
import org.hydrofoil.common.provider.datasource.RowQueryResponse;
import org.hydrofoil.common.provider.datasource.RowStore;

import java.io.Closeable;
import java.util.Collection;
import java.util.Iterator;

/**
 * IDataSource
 * <p>
 * package org.hydrofoil.common.provider
 *
 * @author xie_yh
 * @date 2018/6/29 13:07
 */
public interface IDataSource extends Closeable,AutoCloseable{

    /**
     * scan data set
     * @param query query cond
     * @return result
     */
    RowQueryResponse sendQuery(RowQueryRequest query);

    /**
     * statistics by cond
     * @param query query cond
     * @return total
     */
    Long count(RowQueryRequest query);

}
