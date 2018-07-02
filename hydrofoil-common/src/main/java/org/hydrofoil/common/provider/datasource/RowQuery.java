package org.hydrofoil.common.provider.datasource;

import java.util.function.Predicate;

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
    public void setStart(Long start) {
        this.start = start;
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
    public void setLimit(Long limit) {
        this.limit = limit;
    }

    public abstract static class Q{
        public String field;
        public Object value;

        public static final class eq extends Q{}

        public static final class neq extends Q{}

        public static final class like extends Q{}

        public static final class between extends Q{
            public Object value2;
        }

        public static final class in extends Q{
            public Object value2;
        }
    }

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
    public void setName(String name) {
        this.name = name;
    }
}
