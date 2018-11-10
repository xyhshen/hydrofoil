package org.hydrofoil.common.provider.datasource;

import org.hydrofoil.common.graph.QMatch;
import org.hydrofoil.common.util.DataUtils;

import java.util.Set;

/**
 * RowQuery
 * <p>
 * package org.hydrofoil.common.provider.datasource
 *
 * @author xie_yh
 * @date 2018/7/2 18:34
 */
public final class RowQueryScan extends BaseRowQuery {

    /**
     * query match
     */
    private Set<QMatch.Q> match;

    /**
     * unique field
     */
    private Set<String> uniqueField;

    /**
     * start pos
     */
    private Long offset;

    /**
     * length
     */
    private Long limit;

    public RowQueryScan(){
        super();
        this.uniqueField = DataUtils.newSetWithMaxSize(0);
        this.match = DataUtils.newSetWithMaxSize(0);
        this.offset = 0L;
        this.limit = Long.MAX_VALUE;
    }

    /**
     * @return QMatch
     * @see RowQueryScan#match
     **/
    public Set<QMatch.Q> getMatch() {
        return match;
    }

    /**
     * @return String
     * @see RowQueryScan#uniqueField
     **/
    public Set<String> getUniqueField() {
        return uniqueField;
    }

    /**
     * @return Long
     * @see RowQueryScan#offset
     **/
    public Long getOffset() {
        return offset;
    }

    /**
     * @param offset Long
     * @see RowQueryScan#offset
     **/
    public RowQueryScan setOffset(Long offset) {
        this.offset = offset;
        return this;
    }

    /**
     * @return Long
     * @see RowQueryScan#limit
     **/
    public Long getLimit() {
        return limit;
    }

    /**
     * @param limit Long
     * @see RowQueryScan#limit
     **/
    public RowQueryScan setLimit(Long limit) {
        this.limit = limit;
        return this;
    }

}
