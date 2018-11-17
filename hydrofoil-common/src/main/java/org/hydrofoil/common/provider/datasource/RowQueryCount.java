package org.hydrofoil.common.provider.datasource;

import org.hydrofoil.common.graph.QMatch;
import org.hydrofoil.common.provider.datasource.response.RowCountResponse;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.common.util.ParameterUtils;

import java.util.Set;

/**
 * RowQueryCount
 * <p>
 * package org.hydrofoil.common.provider.datasource
 *
 * @author xie_yh
 * @date 2018/11/8 19:14
 */
public final class RowQueryCount extends BaseRowQuery{

    /**
     * query match
     */
    private Set<QMatch.Q> match;

    /**
     * grouping field
     */
    private String groupFieldName;

    public RowQueryCount(){
        super();
        this.match = DataUtils.newSetWithMaxSize(0);
    }

    @Override
    public RowQueryResponse createResponse(Object o) {
        ParameterUtils.mustTrue(o instanceof Number);
        return new RowCountResponse(getId(), (Long) o);
    }

    /**
     * @return String
     * @see RowQueryCount#groupFieldName
     **/
    public String getGroupFieldName() {
        return groupFieldName;
    }

    /**
     * @param groupFieldName String
     * @see RowQueryCount#groupFieldName
     **/
    public RowQueryCount setGroupFieldName(String groupFieldName) {
        this.groupFieldName = groupFieldName;
        return this;
    }

    /**
     * @return Q>
     * @see RowQueryCount#match
     **/
    public Set<QMatch.Q> getMatch() {
        return match;
    }
}
