package org.hydrofoil.common.provider;

import org.hydrofoil.common.provider.datasource.BaseRowQuery;
import org.hydrofoil.common.provider.datasource.RowQueryGet;
import org.hydrofoil.common.provider.datasource.RowQueryResponse;
import org.hydrofoil.common.provider.datasource.RowQueryScan;
import org.hydrofoil.common.util.DataUtils;

import java.io.Closeable;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;

/**
 * IDataSource
 * <p>
 * package org.hydrofoil.common.provider
 *
 * @author xie_yh
 * @date 2018/6/29 13:07
 */
public interface IDataConnector extends Closeable{

    /**
     *
     */
    enum REQUEST_TYPE{
        GET,
        SCAN,
        COUNT
    }

    String PARAMETER_GROUP_FIELD_NAME = "group";

    /**
     * scan collect set
     * @param query query cond
     * @return result
     */
    default RowQueryResponse scanRow(final RowQueryScan query){
        return DataUtils.iteratorFirst(scanRow(Collections.singleton(query)));
    }

    /**
     * scan collect set
     * @param querySet qyery set
     * @return result set
     */
    default Iterator<RowQueryResponse> scanRow(final Collection<RowQueryScan> querySet){
        return performance(REQUEST_TYPE.SCAN,querySet,Collections.emptyMap());
    }

    /**
     * get row's
     * @param query query cond
     * @return result
     */
    default RowQueryResponse getRows(final RowQueryGet query){
        return DataUtils.iteratorFirst(getRows(Collections.singleton(query)));
    }

    /**
     * batch get row's of collect set
     * @param querySet query set
     * @return resule set
     */
    default Iterator<RowQueryResponse> getRows(final Collection<RowQueryGet> querySet){
        return performance(REQUEST_TYPE.GET,querySet,Collections.emptyMap());
    }

    /**
     * statistics by cond
     * @param query query cond
     * @return total
     */
    default RowQueryResponse countRow(final BaseRowQuery query,String groupFieldName){
        return DataUtils.iteratorFirst(countRow(Collections.singleton(query),groupFieldName));
    }

    default Iterator<RowQueryResponse> countRow(final Collection<BaseRowQuery> querySet,String groupFieldName){
        return performance(REQUEST_TYPE.COUNT,querySet,Collections.singletonMap(PARAMETER_GROUP_FIELD_NAME,groupFieldName));
    }

    /**
     * execute query request
     * @param requestType type
     * @param querySet query set
     * @param parameters parameter's
     * @return response result
     */
    Iterator<RowQueryResponse> performance(final REQUEST_TYPE requestType,final Collection<? extends BaseRowQuery> querySet,final Map<String,Object> parameters);

}
