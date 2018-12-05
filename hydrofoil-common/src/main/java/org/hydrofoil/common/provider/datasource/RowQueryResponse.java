package org.hydrofoil.common.provider.datasource;

import org.apache.commons.collections4.KeyValue;
import org.hydrofoil.common.provider.datasource.response.FailedRowQueryResponse;

import java.sql.SQLException;
import java.util.Collection;

/**
 * RowQueryResponse
 * <p>
 * package org.hydrofoil.common.provider.datasource
 *
 * @author xie_yh
 * @date 2018/11/15 18:30
 */
public interface RowQueryResponse extends AutoCloseable {

    /**
     * get query id
     * @return query id
     */
    Long id();

    /**
     * is performance succeed
     * @return result
     */
    boolean isSucceed();

    /**
     * if failed,return exception
     * @return excepiton
     */
    SQLException getException();

    /**
     * get rows,if
     * @return row store
     */
    Iterable<RowStore> getRows();

    /**
     * return total
     * @return total
     */
    Long count();

    /**
     * return total by group
     * @return group field and count
     */
    Collection<KeyValue<?,Long>> counts();

    static RowQueryResponse createFailedResponse(Long id,SQLException exception){
        return new FailedRowQueryResponse(id,exception);
    }

}
