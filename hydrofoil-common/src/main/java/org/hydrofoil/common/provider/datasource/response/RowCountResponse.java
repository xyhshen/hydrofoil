package org.hydrofoil.common.provider.datasource.response;

import org.hydrofoil.common.provider.datasource.RowStore;

/**
 * RowCountResponse
 * <p>
 * package org.hydrofoil.common.provider.datasource
 *
 * @author xie_yh
 * @date 2018/11/15 19:02
 */
public class RowCountResponse extends AbstractRowQueryResponse {

    private Long count;

    public RowCountResponse(Long id,Long count) {
        super(id,true);
        this.count = count;
    }

    @Override
    public Iterable<RowStore> getRows() {
        return null;
    }

    @Override
    public Long count() {
        return count;
    }
}
