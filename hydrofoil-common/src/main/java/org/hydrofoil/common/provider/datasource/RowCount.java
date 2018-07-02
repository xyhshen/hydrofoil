package org.hydrofoil.common.provider.datasource;

/**
 * RowCount
 * <p>
 * package org.hydrofoil.common.provider.datasource
 *
 * @author xie_yh
 * @date 2018/7/2 18:19
 */
public final class RowCount {

    /**
     * group name
     */
    private String group;

    /**
     * total
     */
    private Long total;

    /**
     * @return String
     * @see RowCount#group
     **/
    public String getGroup() {
        return group;
    }

    /**
     * @param group String
     * @see RowCount#group
     **/
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * @return Long
     * @see RowCount#total
     **/
    public Long getTotal() {
        return total;
    }

    /**
     * @param total Long
     * @see RowCount#total
     **/
    public void setTotal(Long total) {
        this.total = total;
    }
}
