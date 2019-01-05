package org.hydrofoil.common.provider.datasource.response;

import org.apache.commons.collections4.IterableUtils;
import org.hydrofoil.common.provider.datasource.RowStore;
import org.hydrofoil.common.util.ArgumentUtils;

/**
 * RowStoreResponse
 * <p>
 * package org.hydrofoil.common.provider.datasource
 *
 * @author xie_yh
 * @date 2018/11/15 18:51
 */
public class RowStoreResponse extends AbstractRowQueryResponse {

    private Iterable<RowStore> rowIterable;

    public RowStoreResponse(Long id) {
        super(id,true);
        rowIterable = IterableUtils.emptyIterable();
    }

    public RowStoreResponse(Long id,Iterable<RowStore> rowIterable) {
        super(id,true);
        this.rowIterable = rowIterable;
    }

    @Override
    public Iterable<RowStore> getRows() {
        ArgumentUtils.notNull(rowIterable);
        return rowIterable;
    }

    @Override
    public Long count() {
        return (long)IterableUtils.size(rowIterable);
    }
}
