package org.hydrofoil.provider.jdbc.internal;

import org.apache.commons.lang3.StringUtils;

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

    private class Section{
        /**
         * label
         */
        private String label;
        /**
         * sql
         */
        private String sql;
        /**
         * params
         */
        private List<Object> params;

        private Section(String label,String sql,List<Object> params){
            this.label = label;
            this.params = params;
        }
    }

    public SqlStatement(){
        sections = new LinkedList<>();
    }

    public SqlStatement stage(final String sql){
        sections.add(new Section("",sql,null));
        return this;
    }

    @SuppressWarnings("unchecked")
    public SqlStatement stage(final String sql, final List<Object> params){
        sections.add(new Section("",sql,params));
        return this;
    }

    @SuppressWarnings("unchecked")
    public SqlStatement stage(final String label, final String sql,final List<Object> params){
        final Section section = getLocation(label);
        if(section != null){
            section.sql = sql;
            section.params = params;
        }else{
            sections.add(new Section(label,sql,params));
        }
        return this;
    }

    public SqlStatement sentence(final String sentence){
        sections.add(sentence);
        return this;
    }

    public SqlStatement blank(){
        sections.add(" ");
        return this;
    }

    private Section getLocation(String name){
        for(Object sc:sections){
            if(sc instanceof Section){
                Section section = (Section) sc;
                if(StringUtils.equalsIgnoreCase(section.label,name)){
                    return section;
                }
            }
        }
        return null;
    }



    public String toSql(){
        StringBuilder sql = new StringBuilder();
        boolean lastBlank = true;
        for(Object sc:sections){
            String s = null;
            if(sc instanceof Section){
                Section section = (Section) sc;
                s = StringUtils.wrapIfMissing(section.sql," ");
                if(lastBlank){
                    s = StringUtils.removeFirst(s," ");
                }
                sql.append(s);
            }else{
                s = Objects.toString(sc);
                sql.append(s);
            }
            lastBlank = StringUtils.endsWithIgnoreCase(s," ");
        }
        return sql.toString();
    }

    @Override
    public String toString() {
        return toSql();
    }

    public List<Object> getParams(){
        List<Object> l = new ArrayList<>();
        for(Object sc:sections){
            if(sc instanceof Section){
                Section section = (Section) sc;
                List<Object> c = section.params;
                if(c == null){
                    continue;
                }
                l.addAll(c);
            }
        }
        return l;
    }
}
