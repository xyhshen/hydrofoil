package org.hydrofoil.provider.jdbc.internal;

/**
 * SqlFeature
 * <p>
 * package org.hydrofoil.provider.jdbc.internal
 *
 * @author xie_yh
 * @date 2018/12/31 14:01
 */
public final class SqlFeature {

    private int maxPerPage;

    /**
     * @return $field.TypeName
     * @see SqlFeature#maxPerPage
     **/
    public int getMaxPerPage() {
        return maxPerPage;
    }

    /**
     * @param maxPerPage $field.typeName
     * @see SqlFeature#maxPerPage
     **/
    public void setMaxPerPage(int maxPerPage) {
        this.maxPerPage = maxPerPage;
    }
}
