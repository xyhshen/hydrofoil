package org.hydrofoil.common.provider.datasource;

import org.apache.commons.lang3.RandomUtils;
import org.apache.commons.lang3.StringUtils;
import org.hydrofoil.common.graph.QMatch;
import org.hydrofoil.common.provider.datasource.response.RowCountResponse;
import org.hydrofoil.common.util.DataUtils;

import java.util.*;

/**
 * RowRequest
 * <p>
 * package org.hydrofoil.common.provider.datasource
 *
 * @author xie_yh
 * @date 2018/11/8 18:53
 */
public abstract class BaseRowQuery{

    /**
     * Associate match
     */
    public static final class AssociateMatch implements Comparable{

        /**
         * query match
         */
        private QMatch.Q match;

        /**
         * collect set name
         */
        private String name;

        /**
         * field name
         */
        private String joinField;

        public AssociateMatch(
                QMatch.Q match,
                String name,
                String joinField
        ){
            this.match = match;
            this.name = name;
            this.joinField = joinField;
        }

        /**
         * @return Q
         * @see AssociateMatch#match
         **/
        public QMatch.Q getMatch() {
            return match;
        }

        /**
         * @return String
         * @see AssociateMatch#name
         **/
        public String getName() {
            return name;
        }

        /**
         * @return String
         * @see AssociateMatch#joinField
         **/
        public String getJoinField() {
            return joinField;
        }

        @Override
        public boolean equals(Object other) {
            return other instanceof AssociateMatch &&
                    StringUtils.equalsIgnoreCase(((AssociateMatch) other).name, name) &&
                    StringUtils.equalsIgnoreCase(((AssociateMatch) other).joinField, joinField);
        }

        @Override
        public int hashCode(){
            return Objects.hash(match,name, joinField);
        }

        @Override
        public int compareTo(Object o) {
            return Integer.compare(hashCode(),o.hashCode());
        }
    }

    public static final class AssociateRowQuery{
        /**
         * Associate collect set name
         */
        private String name;

        /**
         * Associate match
         */
        private Set<AssociateMatch> match;

        /**
         * one to many(has multi row)
         */
        private boolean oneToMany;

        /**
         * only for queries
         */
        private boolean onlyQueries;

        public AssociateRowQuery(){
            this.match = DataUtils.newSetWithMaxSize(0);
        }

        /**
         * @return String
         * @see AssociateRowQuery#name
         **/
        public String getName() {
            return name;
        }

        /**
         * @param name String
         * @see AssociateRowQuery#name
         **/
        public AssociateRowQuery setName(String name) {
            this.name = name;
            return this;
        }

        /**
         * @return Q>
         * @see AssociateRowQuery#match
         **/
        public Set<AssociateMatch> getMatch() {
            return match;
        }

        /**
         * @return $field.TypeName
         * @see AssociateRowQuery#oneToMany
         **/
        public boolean isOneToMany() {
            return oneToMany;
        }

        /**
         * @param oneToMany $field.typeName
         * @see AssociateRowQuery#oneToMany
         **/
        public AssociateRowQuery setOneToMany(boolean oneToMany) {
            this.oneToMany = oneToMany;
            return this;
        }

        /**
         * @return $field.TypeName
         * @see AssociateRowQuery#onlyQueries
         **/
        public boolean isOnlyQueries() {
            return onlyQueries;
        }

        /**
         * @param onlyQueries $field.typeName
         * @see AssociateRowQuery#onlyQueries
         **/
        public AssociateRowQuery setOnlyQueries(boolean onlyQueries) {
            this.onlyQueries = onlyQueries;
            return this;
        }

        public boolean hasNoneQuery(){
            return match.stream().filter(p->StringUtils.isBlank(p.joinField)).count() > 0;
        }
    }

    /**
     * request id
     */
    private Long id;

    /**
     * collect set name
     */
    private String name;

    /**
     * show fields
     */
    private RowColumnInformation columnInformation;

    /**
     * associate row query
     */
    private List<AssociateRowQuery> associateQuery;

    BaseRowQuery(){
        this.associateQuery = DataUtils.newList();
        this.columnInformation = new RowColumnInformation();
        this.id = RandomUtils.nextLong();
    }

    /**
     * @return String
     * @see BaseRowQuery#name
     **/
    public String getName() {
        return name;
    }

    /**
     * @param name String
     * @see BaseRowQuery#name
     **/
    public BaseRowQuery setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * @return RowColumnInformation
     * @see BaseRowQuery#columnInformation
     **/
    public RowColumnInformation getColumnInformation() {
        return columnInformation;
    }

    /**
     * @return AssociateRowQuery>
     * @see BaseRowQuery#associateQuery
     **/
    public List<AssociateRowQuery> getAssociateQuery() {
        return associateQuery;
    }

    /**
     * @return String
     * @see BaseRowQuery#id
     **/
    public Long getId() {
        return id;
    }

    /**
     * create a row response
     * @param o result,RowStore collect
     * @return response
     */
    public abstract RowQueryResponse createResponse(final Object o);

    /**
     * create count request
     * @param count total
     * @return count response
     */
    public RowCountResponse createCountResponse(final Long count){
        return new RowCountResponse(getId(),count);
    }

    /**
     * create count request by group
     * @param countMap group count
     * @return result
     */
    public RowCountResponse createCountResponse(final Map<String,Long> countMap){
        return new RowCountResponse(getId(),countMap);
    }

}
