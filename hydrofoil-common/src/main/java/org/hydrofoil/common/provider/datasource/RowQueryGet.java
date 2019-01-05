package org.hydrofoil.common.provider.datasource;

import org.hydrofoil.common.provider.datasource.response.RowStoreResponse;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.common.util.ArgumentUtils;
import org.hydrofoil.common.util.bean.KeyValueEntity;

import java.util.Collection;

/**
 * RowQueryGet
 * <p>
 * package org.hydrofoil.common.provider.datasource
 *
 * @author xie_yh
 * @date 2018/11/8 19:07
 */
public final class RowQueryGet extends BaseRowQuery{

    /**
     * rowkey
     */
    private Collection<KeyValueEntity> rowKeys;

    public RowQueryGet(){
        super();
        this.rowKeys = DataUtils.newHashSetWithExpectedSize(10);
    }

    @SuppressWarnings("unchecked")
    @Override
    public RowQueryResponse createResponse(Object o) {
        ArgumentUtils.mustTrue(o instanceof Iterable);
        return new RowStoreResponse(getId(), (Iterable<RowStore>) o);
    }

    /**
     * @return RowKey>
     * @see RowQueryGet#rowKeys
     **/
    public Collection<KeyValueEntity> getRowKeys() {
        return rowKeys;
    }

    /**
     * @param rowkey rowkey
     * @see RowQueryGet#rowKeys
     **/
    public RowQueryGet addRowKey(KeyValueEntity rowkey) {
        this.rowKeys.add(rowkey);
        return this;
    }

}
