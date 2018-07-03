package org.hydrofoil.common.provider.datasource;

import org.hydrofoil.common.graph.QMatch;

import java.util.HashSet;
import java.util.Set;

/**
 * RowQuery
 * <p>
 * package org.hydrofoil.common.provider.datasource
 *
 * @author xie_yh
 * @date 2018/7/2 18:34
 */
public final class RowQuery {

    /**
     * data set name
     */
    private String name;

    /**
     * start pos
     */
    private Long start;

    /**
     * length
     */
    private Long limit;

    /**
     * query match
     */
    private Set<QMatch.Q> match;

    public RowQuery(){
        this.match = new HashSet<>();
    }

    /**
     * @return Long
     * @see RowQuery#start
     **/
    public Long getStart() {
        return start;
    }

    /**
     * @param start Long
     * @see RowQuery#start
     **/
    public RowQuery setStart(Long start) {
        this.start = start;
        return this;
    }

    /**
     * @return Long
     * @see RowQuery#limit
     **/
    public Long getLimit() {
        return limit;
    }

    /**
     * @param limit Long
     * @see RowQuery#limit
     **/
    public RowQuery setLimit(Long limit) {
        this.limit = limit;
        return this;
    }

    /**
     * @return String
     * @see RowQuery#name
     **/
    public String getName() {
        return name;
    }

    /**
     * @param name String
     * @see RowQuery#name
     **/
    public RowQuery setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * @return QMatch
     * @see RowQuery#match
     **/
    public Set<QMatch.Q> getMatch() {
        return match;
    }
}
