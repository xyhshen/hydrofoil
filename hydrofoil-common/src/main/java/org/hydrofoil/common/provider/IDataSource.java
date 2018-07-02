package org.hydrofoil.common.provider;

import org.hydrofoil.common.provider.datasource.RowCount;
import org.hydrofoil.common.provider.datasource.RowQuery;
import org.hydrofoil.common.provider.datasource.RowStore;

import java.io.Closeable;
import java.util.Collection;

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
    Collection<RowStore> scan(RowQuery query);

    /**
     * statistics by cond
     * @param query query cond
     * @return total
     */
    Long count(RowQuery query);

}
