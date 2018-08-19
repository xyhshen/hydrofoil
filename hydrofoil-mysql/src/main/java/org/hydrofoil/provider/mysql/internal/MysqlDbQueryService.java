package org.hydrofoil.provider.mysql.internal;

import org.apache.commons.collections4.MultiMapUtils;
import org.apache.commons.dbcp2.BasicDataSource;
import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.hydrofoil.common.graph.QMatch;
import org.hydrofoil.common.provider.datasource.RowQueryRequest;
import org.hydrofoil.common.util.bean.FieldTriple;

import java.util.List;
import java.util.Objects;

/**
 * MysqlDbQueryService
 * <p>
 * package org.hydrofoil.provider.mysql.internal
 *
 * @author xie_yh
 * @date 2018/7/6 10:24
 */
public final class MysqlDbQueryService extends AbstractDbQueryService{

    public MysqlDbQueryService(BasicDataSource dataSource, RowQueryRequest request) {
        super(dataSource, request);
    }

    /**
     * get column list,to xxx as yyy
     * @return sql
     */
    private String getColumnList(){
        StringBuilder sql = new StringBuilder();
        MutableBoolean first = new MutableBoolean(true);
        tableAlias.forEach((tableName,tableAliasName)->{
            MultiMapUtils.getCollection(columns,tableName).forEach((columnName)->{
                if(first.isFalse()){
                    sql.append(",");
                }
                /*if(StringUtils.equalsIgnoreCase(request.getName(),tableName) &&
                        request.getUniqueField().contains(columnName)){
                    sql.append("distinct ");
                }*/
                sql.append(tableAliasName).
                        append(".").
                        append(columnName).
                        append(" as ").
                        append(columnAlias.get(tableName).get(columnName));
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
    private String toWhereSection(String tableName,QMatch.Q q, String joinField, List<String> params){
        StringBuilder sql = new StringBuilder();
        if(q.type() == QMatch.QType.eq){
            sql.append(getColumnAlias(tableName,q.pair().name()))
                    .append(q.isNot()?"!=":"=")
                    .append(StringUtils.isNotBlank(joinField)?joinField:"?");
            if(StringUtils.isBlank(joinField)){
                params.add(Objects.toString(q.pair().first(),""));
            }
        }
        if(q.type() == QMatch.QType.like){
            sql.append(getColumnAlias(tableName,q.pair().name()))
                    .append(q.isNot()?" not like ":" like ")
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
            sql.append(getColumnAlias(tableName,q.pair().name()))
                    .append(" ").append(symbol).append(" ")
                    .append(StringUtils.isNotBlank(joinField)?joinField:"?");
            if(StringUtils.isBlank(joinField)){
                params.add(Objects.toString(q.pair().first(),""));
            }
        }
        if(q.type() == QMatch.QType.between){
            sql.append(getColumnAlias(tableName,q.pair().name()))
                    .append(q.isNot()?" not between ":" between ")
                    .append("? and ? ");
            params.add(Objects.toString(q.pair().first(),""));
            params.add(Objects.toString(((FieldTriple)q.pair()).last(),""));
        }
        if(q.type() == QMatch.QType.in){
            sql.append(getColumnAlias(tableName,q.pair().name()))
                    .append(q.isNot()?" not in ":" in ")
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
    private String createAssociateQuerySql(List<String> params){
        StringBuilder sql = new StringBuilder();
        request.getAssociateQuery().forEach((rowQuery)->{
            sql.append("inner join ").append(rowQuery.getName()).
                    append(" as ").append(tableAlias.get(rowQuery.getName()));
            if(!rowQuery.getMatch().isEmpty()){
                sql.append(" on ");
                MutableBoolean first = new MutableBoolean(true);
                rowQuery.getMatch().forEach((q)->{
                    if(first.isFalse()){
                        sql.append(" and ");
                    }
                    sql.append(toWhereSection(rowQuery.getName(),q.getMatch(),getColumnAlias(q.getName(),q.getFieldname()),params));
                });
            }
        });
        return sql.toString();
    }

    @Override
    protected String crateQuerySql(List<String> params) {
        StringBuilder sql = new StringBuilder();
        sql.append("select ");
        sql.append(getColumnList());
        sql.append(" from ").
                append(request.getName()).
                append(" as ").
                append(tableAlias.get(request.getName()));
        sql.append(" ").append(createAssociateQuerySql(params));
        if(!request.getMatch().isEmpty()){
            sql.append(" where ");
            MutableBoolean first = new MutableBoolean(true);
            request.getMatch().forEach((q)->{
                if(first.isFalse()){
                    sql.append(" and ");
                }
                sql.append(toWhereSection(request.getName(),q,null,params));
                first.setFalse();
            });
        }
        if(ObjectUtils.allNotNull(request.getOffset(),request.getLimit())){
            sql.append(" limit ").
                    append(request.getOffset()).
                    append(",").
                    append(request.getLimit());
        }
        return sql.toString();
    }

    @Override
    protected String crateCountSql(List<String> params) {
        return null;
    }
}
