package org.hydrofoil.provider.jdbc;

import org.hydrofoil.common.provider.datasource.BaseRowQuery;
import org.hydrofoil.common.provider.datasource.RowQueryGet;
import org.hydrofoil.common.provider.datasource.RowQueryResponse;
import org.hydrofoil.common.provider.datasource.RowQueryScan;

/**
 * IJdbcService
 * <p>
 * package org.hydrofoil.provider.jdbc
 *
 * @author xie_yh
 * @date 2018/12/31 10:11
 */
public interface IJdbcService {

    /**
     * scan rows
     * @param rowQueryScan query scan
     * @return result
     */
    RowQueryResponse scanRows(final RowQueryScan rowQueryScan);

    /**
     * get rows
     * @param rowQueryGet query get
     * @return result
     */
    RowQueryResponse getRows(final RowQueryGet rowQueryGet);

    /**
     * count
     * @param baseRowQuery query
     * @param groupField group field
     * @return result
     */
    RowQueryResponse countRow(final BaseRowQuery baseRowQuery, String groupField);

}
