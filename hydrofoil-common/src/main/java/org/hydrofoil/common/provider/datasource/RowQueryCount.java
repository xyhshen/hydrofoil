package org.hydrofoil.common.provider.datasource;

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
     * grouping field
     */
    private String groupFieldName;

    public RowQueryCount(){
        super();
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
}
