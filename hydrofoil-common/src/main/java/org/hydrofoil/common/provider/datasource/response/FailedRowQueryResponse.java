package org.hydrofoil.common.provider.datasource.response;

import org.hydrofoil.common.provider.datasource.RowStore;

import java.sql.SQLException;

/**
 * FailedRowQueryResponse
 * <p>
 * package org.hydrofoil.common.provider.datasource.response
 *
 * @author xie_yh
 * @date 2018/11/15 19:12
 */
public final class FailedRowQueryResponse extends AbstractRowQueryResponse{

    public FailedRowQueryResponse(Long id, SQLException exception) {
        super(id, false);
        setException(exception);
    }

    @Override
    public Iterable<RowStore> getRows() {
        return null;
    }

    @Override
    public Long count() {
        return null;
    }
}
