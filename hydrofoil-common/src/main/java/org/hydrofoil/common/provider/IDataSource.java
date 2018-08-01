package org.hydrofoil.common.provider;

import org.hydrofoil.common.provider.datasource.RowQueryRequest;
import org.hydrofoil.common.provider.datasource.RowQueryResponse;
import org.hydrofoil.common.util.DataUtils;

import java.io.Closeable;
import java.util.Collection;
import java.util.Collections;
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
     * scan collect set
     * @param query query cond
     * @return result
     */
    default RowQueryResponse sendQuery(final RowQueryRequest query){
        return DataUtils.iteratorFirst(sendQuery(Collections.singleton(query)));
    }

    /**
     * scan collect set
     * @param querySet qyery set
     * @return result set
     */
    Iterator<RowQueryResponse> sendQuery(final Collection<RowQueryRequest> querySet);

    /**
     * statistics by cond
     * @param query query cond
     * @return total
     */
    Long count(final RowQueryRequest query);

}
