package org.hydrofoil.common.provider.datasource;

/**
 * RowQueryCountResponse
 * <p>
 * package org.hydrofoil.common.provider.datasource
 *
 * @author xie_yh
 * @date 2018/7/2 18:19
 */
public final class RowQueryCountResponse {

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
     * @see RowQueryCountResponse#group
     **/
    public String getGroup() {
        return group;
    }

    /**
     * @param group String
     * @see RowQueryCountResponse#group
     **/
    public void setGroup(String group) {
        this.group = group;
    }

    /**
     * @return Long
     * @see RowQueryCountResponse#total
     **/
    public Long getTotal() {
        return total;
    }

    /**
     * @param total Long
     * @see RowQueryCountResponse#total
     **/
    public void setTotal(Long total) {
        this.total = total;
    }
}
