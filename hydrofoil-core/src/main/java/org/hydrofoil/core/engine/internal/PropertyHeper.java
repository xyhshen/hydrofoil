package org.hydrofoil.core.engine.internal;

import org.apache.commons.collections4.*;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.hydrofoil.common.graph.QMatch;
import org.hydrofoil.common.provider.datasource.BaseRowQuery;
import org.hydrofoil.common.schema.BaseElementSchema;
import org.hydrofoil.common.schema.LinkSchema;
import org.hydrofoil.common.schema.PropertySchema;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.common.util.ParameterUtils;

import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 * PropertyHeper
 * <p>
 * package org.hydrofoil.core.engine.internal
 *
 * @author xie_yh
 * @date 2018/11/17 16:18
 */
interface PropertyHeper extends MapperHelper {

    default boolean invalidProperties(final Set<QMatch.Q> propertyQuerySet, final BaseElementSchema elementSchema) {
        final Set<String> labels = elementSchema.getProperties().keySet();
        if (CollectionUtils.isEmpty(propertyQuerySet)) {
            return false;
        }
        return propertyQuerySet.stream().
                filter(p -> labels.contains(p.pair().name())).count() == 0;
    }

    default PropertyQueryCondition createPropertyQueryCondtion(final Set<QMatch.Q> propertyQuerySet, final BaseElementSchema elementSchema) {
        PropertyQueryCondition queryCondition = new PropertyQueryCondition();
        if (invalidProperties(propertyQuerySet, elementSchema)) {
            return null;
        }
        Set<QMatch.Q> mainCondition = queryCondition.getMainCondition();
        MultiValuedMap<String, BaseRowQuery.AssociateMatch> associateQueryCondition = queryCondition.getAssociateQueryCondition();
        SetUtils.emptyIfNull(propertyQuerySet).stream().
                filter(propertyQuery -> checkQueriable(elementSchema, propertyQuery)).
                forEach((propertyQuery) -> {
                    PropertySchema propertySchema = elementSchema.getProperties().get(propertyQuery.pair().name());
                    if (propertySchema == null) {
                        return;
                    }
                    QMatch.Q clone = propertyQuery.clone();
                    clone.pair().name(propertySchema.getField());
                    if (StringUtils.isNotBlank(propertySchema.getLinkTable())) {
                        LinkSchema linkSchema = elementSchema.getLinks().get(propertySchema.getLinkTable());
                        ParameterUtils.notNull(linkSchema);
                        associateQueryCondition.put(linkSchema.getTable(), new BaseRowQuery.AssociateMatch(clone, linkSchema.getTable(), null));
                    } else {
                        mainCondition.add(clone);
                    }
                });
        return queryCondition;
    }

    default void setRowQueryProperties(final BaseRowQuery rowQueryRequest, final Map<String, BaseRowQuery.AssociateRowQuery> associateRowQueryMap,final Collection<String> hasLinkTables, final BaseElementSchema elementSchema) {
        elementSchema.getProperties().forEach((propertyLabel, propertySchema) -> {
            if (isPropertyInMainTable(propertySchema)) {
                rowQueryRequest.getColumnInformation().column(elementSchema.getTable(), propertySchema.getField());
            } else {
                if(hasLinkTables != null
                        && !hasLinkTables.contains(propertySchema.getLinkTable())){
                    return;
                }
                BaseRowQuery.AssociateRowQuery associateRowQuery =
                        associateRowQueryMap.computeIfAbsent(propertySchema.getLinkTable(),
                                (v) -> new BaseRowQuery.AssociateRowQuery().setName(v));
                if (MapUtils.isEmpty(propertySchema.getChildren())) {
                    rowQueryRequest.getColumnInformation().column(associateRowQuery.getName(), propertySchema.getField());
                } else {
                    propertySchema.getChildren().forEach(((name, pairSchema) -> rowQueryRequest.getColumnInformation().
                            column(associateRowQuery.getName(), pairSchema.getField())));
                }
                /*
                        get associate column
                    */
                final LinkSchema linkSchema = elementSchema.getLinks().get(propertySchema.getLinkTable());
                associateRowQuery.setOneToMany(linkSchema.isOneToMany());
                associateRowQuery.setOnlyQueries(linkSchema.isOnlyQuery());
                linkSchema.getJoinfield().forEach((field1, field2) -> {
                    associateRowQuery.getMatch().add(new BaseRowQuery.
                            AssociateMatch(
                            QMatch.eq(field2, null),
                            rowQueryRequest.getName(),
                            field1
                    ));
                    rowQueryRequest.getColumnInformation().column(associateRowQuery.getName(), field2);
                });
            }
        });
        rowQueryRequest.getAssociateQuery().addAll(associateRowQueryMap.values());
    }

    default Map<String, Pair<String, String>> getPropertyToFieldMapOfElement(final BaseElementSchema elementSchema) {
        final Map<String, PropertySchema> properties = elementSchema.getProperties();
        Map<String, Pair<String, String>> property2Field = DataUtils.newHashMapWithExpectedSize(properties.size());
        properties.forEach((label, propertySchema) -> {
            property2Field.put(label, Pair.of(propertySchema.getLinkTable(), propertySchema.getField()));
        });
        return property2Field;
    }
}

final class PropertyQueryCondition {

    /**
     * main cond
     */
    private Set<QMatch.Q> mainCondition = DataUtils.newHashSetWithExpectedSize(1);

    /**
     * associate cond
     */
    private MultiValuedMap<String, BaseRowQuery.AssociateMatch> associateQueryCondition = MultiMapUtils.newSetValuedHashMap();

    /**
     * @return Q>
     * @see PropertyQueryCondition#mainCondition
     **/
    Set<QMatch.Q> getMainCondition() {
        return mainCondition;
    }

    /**
     * @return AssociateMatch>
     * @see PropertyQueryCondition#associateQueryCondition
     **/
    MultiValuedMap<String, BaseRowQuery.AssociateMatch> getAssociateQueryCondition() {
        return associateQueryCondition;
    }
}
