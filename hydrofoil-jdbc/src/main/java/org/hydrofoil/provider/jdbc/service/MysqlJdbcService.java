package org.hydrofoil.provider.jdbc.service;

import org.apache.commons.collections4.MultiMapUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.hydrofoil.common.graph.QMatch;
import org.hydrofoil.common.provider.IDataConnectContext;
import org.hydrofoil.common.provider.datasource.BaseRowQuery;
import org.hydrofoil.common.provider.datasource.RowQueryGet;
import org.hydrofoil.common.schema.ColumnSchema;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.common.util.SqlUtils;
import org.hydrofoil.common.util.bean.FieldTriple;
import org.hydrofoil.common.util.bean.KeyValueEntity;
import org.hydrofoil.provider.jdbc.internal.AbstractJdbcService;
import org.hydrofoil.provider.jdbc.internal.QueryContext;
import org.hydrofoil.provider.jdbc.internal.SqlFeature;
import org.hydrofoil.provider.jdbc.internal.SqlStatement;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * MysqlDbQueryService
 * <p>
 * package org.hydrofoil.provider.jdbc.internal
 *
 * @author xie_yh
 * @date 2018/7/6 10:24
 */
public final class MysqlJdbcService extends AbstractJdbcService{

    public MysqlJdbcService(DataSource dataSource, IDataConnectContext dataSourceContext) {
        super(dataSource,dataSourceContext);
        sqlFeature = new SqlFeature();
        sqlFeature.setMaxPerPage(500);
    }

    /**
     * get column list,to xxx as yyy
     * @return sql
     */
    private String getColumnList(QueryContext context){
        StringBuilder sql = new StringBuilder();
        MutableBoolean first = new MutableBoolean(true);
        context.getTableAlias().forEach((tableName,tableAliasName)->{
            MultiMapUtils.getCollection(context.getColumns(),tableName).forEach((columnName)->{
                if(first.isFalse()){
                    sql.append(",");
                }
                sql.append(tableAliasName).
                        append(".").
                        append(columnName).
                        append(" ").
                        append(context.getColumnAlias().get(tableName).get(columnName));
                first.setFalse();
            });
        });
        return sql.toString();
    }

    /**
     * query to where section
     * @param tableName table name
     * @param q query
     * @param joinField join field
     * @param params params
     * @return sql
     */
    @SuppressWarnings("unchecked")
    private String toWhereSection(QueryContext context,String tableName,QMatch.Q q, String joinField, List<String> params){
        StringBuilder sql = new StringBuilder();
        if(q.type() == QMatch.QType.eq){
            sql.append(context.getColumnAlias(tableName,q.pair().name()))
                    .append("=")
                    .append(StringUtils.isNotBlank(joinField)?joinField:"?");
            if(StringUtils.isBlank(joinField)){
                params.add(Objects.toString(q.pair().first(),""));
            }
        }
        if(q.type() == QMatch.QType.prefix){
            sql.append(context.getColumnAlias(tableName,q.pair().name()))
                    .append(" like ")
                    .append(StringUtils.isNotBlank(joinField)?joinField:"?");
            if(StringUtils.isBlank(joinField)){
                params.add(Objects.toString(q.pair().first(),"") + "%");
            }
        }
        if(q.type() == QMatch.QType.gt ||
                q.type() == QMatch.QType.gte ||
                q.type() == QMatch.QType.lt ||
                q.type() == QMatch.QType.lte){
            String symbol = "";
            if(q.type() == QMatch.QType.gt){
                symbol = ">";
            }
            if(q.type() == QMatch.QType.gte){
                symbol = ">=";
            }
            if(q.type() == QMatch.QType.lt){
                symbol = "<";
            }
            if(q.type() == QMatch.QType.lte){
                symbol = "<=";
            }
            sql.append(context.getColumnAlias(tableName,q.pair().name()))
                    .append(" ").append(symbol).append(" ")
                    .append(StringUtils.isNotBlank(joinField)?joinField:"?");
            if(StringUtils.isBlank(joinField)){
                params.add(Objects.toString(q.pair().first(),""));
            }
        }
        if(q.type() == QMatch.QType.between){
            sql.append(context.getColumnAlias(tableName,q.pair().name()))
                    .append(" between ")
                    .append("? and ? ");
            params.add(Objects.toString(q.pair().first(),""));
            params.add(Objects.toString(((FieldTriple)q.pair()).last(),""));
        }
        if(q.type() == QMatch.QType.in){
            sql.append(context.getColumnAlias(tableName,q.pair().name()))
                    .append(" in ")
                    .append("(");
            List<Object> l = (List<Object>) q.pair().first();
            MutableBoolean first = new MutableBoolean(true);
            l.forEach((v)->{
                if(first.isFalse()){
                    sql.append(",");
                }
                sql.append("?");
                params.add(Objects.toString(v,""));
                first.setFalse();
            });
            sql.append(")");
        }
        return sql.toString();
    }

    /**
     * create Associat query sql
     * @param params params
     * @return sql
     */
    private String createAssociateGetSql(BaseRowQuery rowQuery,QueryContext context, List<String> params){
        StringBuilder sql = new StringBuilder();
        rowQuery.getAssociateQuery().forEach((associateQuery)->{
            if(associateQuery.isOnlyQueries() || associateQuery.isOneToMany()){
               return;
            }
            sql.append("left join ").append(StringUtils.wrap(toRealName(associateQuery.getName()),"'")).
                    append(" ").append(context.getTableAlias().get(toRealName(associateQuery.getName())));
            if(!associateQuery.getMatch().isEmpty()){
                sql.append(" on ");
                MutableBoolean first = new MutableBoolean(true);
                associateQuery.getMatch().forEach((q)->{
                    if(first.isFalse()){
                        sql.append(" and ");
                    }
                    sql.append(toWhereSection(context,toRealName(associateQuery.getName()),q.getMatch(),context.getColumnAlias(toRealName(q.getName()),q.getJoinField()),params));
                });
            }
        });
        return sql.toString();
    }

    @Override
    protected SqlStatement createBasicGetSql(RowQueryGet rowQuery, QueryContext context) {
        SqlStatement sqlStatement = new SqlStatement();
        sqlStatement.append("select ");
        sqlStatement.append(getColumnList(context));
        sqlStatement.append(" from (").
                location("keys").
                append(")").
                blank().
                append(context.getTableAlias().get(toRealName(rowQuery.getName())));
        sqlStatement.blank().append(createAssociateGetSql(rowQuery,context,null));

        /*if(!rowQuery.getMatch().isEmpty()){
            sql.append(" where ");
            MutableBoolean first = new MutableBoolean(true);
            rowQuery.getMatch().forEach((q)->{
                if(first.isFalse()){
                    sql.append(" and ");
                }
                sql.append(toWhereSection(rowQuery.getName(),q,null,params));
                first.setFalse();
            });
        }
        if(ObjectUtils.allNotNull(rowQuery.getOffset(),rowQuery.getLimit())){
            sql.append(" limit ").
                    append(rowQuery.getOffset()).
                    append(",").
                    append(rowQuery.getLimit());
        }*/
        return sqlStatement;
    }

    @Override
    protected void makeGetKeySql(String tableName, SqlStatement basicSql, QueryContext context, Collection<KeyValueEntity> keyValueEntities) {
        final KeyValueEntity.KeyValueEntityFactory factory = DataUtils.collectFirst(keyValueEntities).getFactory();
        StringBuilder sql = new StringBuilder();
        sql.append("select ").
                append(" from ").
                append(StringUtils.wrap(tableName,"'")).
                append(" where ");
        boolean singleKey = factory.keys().size() == 1;
        String keyName = null;
        if(singleKey){
            keyName = DataUtils.collectFirst(factory.keys());
            sql.append(keyName);
        }else{
            sql.append("(");
            for(String key:factory.keys()){
                sql.append(key).append(",");
            }
            sql.deleteCharAt(sql.length() - 1);
            sql.append(")");
        }
        List<Object> params = new ArrayList<>();
        sql.append(" in (");
        for(KeyValueEntity keyValue:keyValueEntities){
            if(singleKey){
                sql.append("?");
                params.add(keyValue.getKeyValueMap().get(keyName));
            }else{
                sql.append("(");
                for(String key:factory.keys()){
                    sql.append("?").append(",");
                    params.add(keyValue.getKeyValueMap().get(key));
                }
                sql.deleteCharAt(sql.length() - 1);
                sql.append(")");
            }
            sql.append(",");
        }
        sql.deleteCharAt(sql.length() - 1);
        sql.append(")");
        basicSql.set("keys",sql.toString(),params);
    }

    @Override
    protected String crateCountSql(List<String> params) {
        return null;
    }

    @Override
    protected Object handleQueryResult(ColumnSchema columnSchema, Object value) throws SQLException {
        try {
            return SqlUtils.rawDataToAcceptData(columnSchema,value);
        } catch (Exception e) {
            throw new SQLException(e);
        }
    }
}
