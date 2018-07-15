package org.hydrofoil.common.provider.datasource;

import org.apache.commons.lang3.RandomUtils;
import org.hydrofoil.common.graph.QMatch;

import java.util.*;

/**
 * RowQuery
 * <p>
 * package org.hydrofoil.common.provider.datasource
 *
 * @author xie_yh
 * @date 2018/7/2 18:34
 */
public final class RowQueryRequest {


    /**
     * Associate match
     */
    public static final class AssociateMatch{

        /**
         * query match
         */
        private QMatch.Q match;

        /**
         * data set name
         */
        private String name;

        /**
         * field name
         */
        private String fieldname;

        public AssociateMatch(
                QMatch.Q match,
                String name,
                String fieldname
        ){
            this.match = match;
            this.name = name;
            this.fieldname = fieldname;
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
         * @see AssociateMatch#fieldname
         **/
        public String getFieldname() {
            return fieldname;
        }
    }

    public static final class AssociateRowQuery{
        /**
         * Associate data set name
         */
        private String name;
        /**
         * Associate match
         */
        private Set<AssociateMatch> match;

        /**
         * fields
         */
        private Set<String> fields;

        public AssociateRowQuery(){
            this.match = new HashSet<>();
            this.fields = new HashSet<>();
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
         * @return String>
         * @see AssociateRowQuery#fields
         **/
        public Set<String> getFields() {
            return fields;
        }
    }

    /**
     * request id
     */
    private Long id;

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

    /**
     * show fields
     */
    private Set<String> fields;

    /**
     * unique field
     */
    private Set<String> uniqueField;

    /**
     * associate row query
     */
    private List<AssociateRowQuery> associateQuery;

    public RowQueryRequest(){
        this.match = new HashSet<>();
        this.uniqueField = new TreeSet<>();
        this.associateQuery = new ArrayList<>(2);
        this.fields = new TreeSet<>();
        this.id = RandomUtils.nextLong();
    }

    /**
     * @return Long
     * @see RowQueryRequest#start
     **/
    public Long getStart() {
        return start;
    }

    /**
     * @param start Long
     * @see RowQueryRequest#start
     **/
    public RowQueryRequest setStart(Long start) {
        this.start = start;
        return this;
    }

    /**
     * @return Long
     * @see RowQueryRequest#limit
     **/
    public Long getLimit() {
        return limit;
    }

    /**
     * @param limit Long
     * @see RowQueryRequest#limit
     **/
    public RowQueryRequest setLimit(Long limit) {
        this.limit = limit;
        return this;
    }

    /**
     * @return String
     * @see RowQueryRequest#name
     **/
    public String getName() {
        return name;
    }

    /**
     * @param name String
     * @see RowQueryRequest#name
     **/
    public RowQueryRequest setName(String name) {
        this.name = name;
        return this;
    }

    /**
     * @return QMatch
     * @see RowQueryRequest#match
     **/
    public Set<QMatch.Q> getMatch() {
        return match;
    }

    /**
     * @return String>
     * @see RowQueryRequest#uniqueField
     **/
    public Set<String> getUniqueField() {
        return uniqueField;
    }

    /**
     * @return AssociateRowQuery>
     * @see RowQueryRequest#associateQuery
     **/
    public List<AssociateRowQuery> getAssociateQuery() {
        return associateQuery;
    }

    /**
     * @return String>
     * @see RowQueryRequest#fields
     **/
    public Set<String> getFields() {
        return fields;
    }

    /**
     * @return String
     * @see RowQueryRequest#id
     **/
    public Long getId() {
        return id;
    }
}
