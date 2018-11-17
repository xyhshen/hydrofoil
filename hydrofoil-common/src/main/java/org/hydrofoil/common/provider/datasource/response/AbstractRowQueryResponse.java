package org.hydrofoil.common.provider.datasource.response;

import org.hydrofoil.common.provider.datasource.RowQueryResponse;

import java.sql.SQLException;

/**
 * RowQueryResponse
 * <p>
 * package org.hydrofoil.common.provider.datasource
 *
 * @author xie_yh
 * @date 2018/7/4 10:36
 */
public abstract class AbstractRowQueryResponse implements RowQueryResponse {

    /**
     * request id
     */
    private final Long id;

    /**
     * state
     */
    private final boolean succeed;

    /**
     * exception
     */
    private SQLException exception;

    protected AbstractRowQueryResponse(Long id,boolean succeed){
        this.succeed = succeed;
        this.id = id;
    }

    @Override
    public void close() throws Exception {}

    /**
     * @return $field.TypeName
     * @see AbstractRowQueryResponse#succeed
     **/
    @Override
    public boolean isSucceed() {
        return succeed;
    }

    /**
     * @return SQLException
     * @see AbstractRowQueryResponse#exception
     **/
    @Override
    public SQLException getException() {
        return exception;
    }

    /**
     * @param exception SQLException
     * @see AbstractRowQueryResponse#exception
     **/
    public void setException(SQLException exception) {
        this.exception = exception;
    }

    /**
     * @return Long
     * @see AbstractRowQueryResponse#id
     **/
    @Override
    public Long id() {
        return id;
    }
}
