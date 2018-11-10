package org.hydrofoil.common.provider.datasource;

import org.hydrofoil.common.util.DataUtils;

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
    private Collection<RowKey> rowKeys;

    public RowQueryGet(){
        super();
        this.rowKeys = DataUtils.newList();
    }

    /**
     * @return RowKey>
     * @see RowQueryGet#rowKeys
     **/
    public Collection<RowKey> getRowKeys() {
        return rowKeys;
    }

    /**
     * @param rowkey rowkey
     * @see RowQueryGet#rowKeys
     **/
    public RowQueryGet addRowKey(RowKey rowkey) {
        this.rowKeys.add(rowkey);
        return this;
    }

}
