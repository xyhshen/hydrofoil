package org.hydrofoil.common.provider.datasource;

import java.io.Closeable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * RowQueryResponse
 * <p>
 * package org.hydrofoil.common.provider.datasource
 *
 * @author xie_yh
 * @date 2018/7/4 10:36
 */
public class RowQueryResponse implements Iterator<RowStore>,AutoCloseable{

    /**
     * state
     */
    private boolean succeed;

    /**
     * default row store
     */
    private Collection<RowStore> defaultRowStore;

    public RowQueryResponse(boolean succeed){
        this.succeed = succeed;
        this.defaultRowStore = new ArrayList<>(5);
    }

    @Override
    public boolean hasNext() {
        return defaultRowStore.iterator().hasNext();
    }

    @Override
    public RowStore next() {
        return defaultRowStore.iterator().next();
    }

    @Override
    public void close() throws Exception {}

    /**
     * @return $field.TypeName
     * @see RowQueryResponse#succeed
     **/
    public boolean isSucceed() {
        return succeed;
    }

    /**
     * @return RowStore>
     * @see RowQueryResponse#defaultRowStore
     **/
    public Collection<RowStore> getDefaultRowStore() {
        return defaultRowStore;
    }
}
