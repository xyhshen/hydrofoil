package org.hydrofoil.common.provider.datasource;

import org.hydrofoil.common.provider.datasource.response.FailedRowQueryResponse;

import java.sql.SQLException;

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

    static RowQueryResponse createFailedResponse(Long id,SQLException exception){
        return new FailedRowQueryResponse(id,exception);
    }

}
