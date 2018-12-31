package org.hydrofoil.provider.jdbc.internal;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.MutableTriple;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * SqlStatement
 * <p>
 * package org.hydrofoil.provider.jdbc.internal
 *
 * @author xie_yh
 * @date 2018/12/31 14:42
 */
public final class SqlStatement {

    private final List<Object> sections;

    public SqlStatement(){
        sections = new LinkedList<>();
    }

    public SqlStatement append(final String section){
        sections.add(section);
        return this;
    }

    @SuppressWarnings("unchecked")
    public SqlStatement append(final String section, List<Object> params){
        sections.add(new MutableTriple("",section,params));
        return this;
    }

    public SqlStatement blank(){
        sections.add(" ");
        return this;
    }

    @SuppressWarnings("unchecked")
    public SqlStatement location(final String name){
        sections.add(new MutableTriple(name,null,new ArrayList<>()));
        return this;
    }

    private MutableTriple getLocation(String name){
        for(Object sc:sections){
            if(sc instanceof Triple){
                MutableTriple triple = (MutableTriple) sc;
                if(StringUtils.equalsIgnoreCase(triple.getLeft().toString(),name)){
                    return triple;
                }
            }
        }
        return null;
    }

    @SuppressWarnings("unchecked")
    public SqlStatement set(final String name, String section,List<Object> params){
        final MutableTriple triple = getLocation(name);
        if(triple != null){
            triple.setMiddle(section);
            triple.setRight(params);
        }
        return this;
    }

    public String toSql(){
        StringBuilder sql = new StringBuilder();
        for(Object sc:sections){
            if(sc instanceof Triple){
                MutableTriple triple = (MutableTriple) sc;
                sql.append(Objects.toString(triple.getMiddle()));
            }else{
                sql.append(Objects.toString(sc));
            }
        }
        return sql.toString();
    }

    public List<Object> getParams(){
        List<Object> l = new ArrayList<>();
        for(Object sc:sections){
            if(sc instanceof Triple){
                MutableTriple triple = (MutableTriple) sc;
                List<Object> c = (List<Object>) triple.getRight();
                if(c == null){
                    continue;
                }
                l.addAll(c);
            }
        }
        return l;
    }
}
