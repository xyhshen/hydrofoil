package org.hydrofoil.common.provider.datasource;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.common.util.bean.FieldPair;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * RowStore
 * <p>
 * package org.hydrofoil.common.provider.datasource
 *
 * @author xie_yh
 * @date 2018/7/4 10:41
 */
public final class RowStore {

    private final Object[] rowFull;

    private final RowColumnInformation information;

    private final String collectSetName;

    RowStore(final RowColumnInformation informatio){
        this.rowFull = new Object[informatio.size()];
        this.information = informatio;
        this.collectSetName = null;
    }

    private RowStore(final RowColumnInformation information, final String collectSetName){
        this.information = information;
        this.collectSetName = collectSetName;
        this.rowFull = new Object[information.size(collectSetName)];
    }

    /**
     * put a sub row field
     * @param fieldname field name
     * @param value value
     * @return this
     */
    public RowStore put(final String fieldname,final Object value){
        ParameterUtils.notBlank(collectSetName);
        Integer fieldIndex =  information.index(collectSetName,fieldname);
        rowFull[fieldIndex] = value;
        return this;
    }

    /**
     * put field
     * @param name collect set name
     * @param pair field pair
     * @return this
     */
    public RowStore put(final String name, final FieldPair pair){
        Integer collectIndex = information.index(name);
        Integer fieldIndex =  information.index(name,pair.name());
        ParameterUtils.mustTrue(ObjectUtils.allNotNull(collectIndex,fieldIndex));
        Object o = rowFull[collectIndex];
        if(o == null || o instanceof Collection){
            rowFull[collectIndex] = new Object[information.size(name)];
            o = rowFull[collectIndex];
        }
        Object[] cells = (Object[]) o;
        cells[fieldIndex] = pair.first();
        return this;
    }

    /**
     * put a field value
     * @param name collect set name
     * @param fieldname field name
     * @param value field value
     * @return this
     */
    public RowStore put(final String name,final String fieldname,final Object value){
        return put(name,new FieldPair(fieldname,value));
    }

    /**
     * create a sub row
     * @param name collect name
     * @return new sub row
     */
    @SuppressWarnings("unchecked")
    public RowStore createRow(final String name){
        Integer collectIndex = information.index(name);
        ParameterUtils.notNull(collectIndex);
        Object o = rowFull[collectIndex];
        if(o == null || !(o instanceof Collection)){
            rowFull[collectIndex] = new ArrayList();
            o = rowFull[collectIndex];
        }
        List<RowStore> r = (List<RowStore>) o;
        RowStore row = new RowStore(information,name);
        r.add(row);
        return row;
    }

    /**
     * get field
     * @param name field
     * @param fieldname fieldname
     * @return field pair
     */
    public FieldPair field(final String name,final String fieldname){
        if(StringUtils.isNotBlank(collectSetName)){
            Integer fieldIndex =  information.index(collectSetName,fieldname);
            return new FieldPair(fieldname,rowFull[fieldIndex]);
        }else{
            Integer collectIndex = information.index(name);
            Integer fieldIndex =  information.index(name,fieldname);
            if(!ObjectUtils.allNotNull(collectIndex,fieldIndex)){
                return null;
            }
            Object o = rowFull[collectIndex];
            if(o == null || o instanceof Collection){
                return null;
            }
            Object[] cells = (Object[]) o;
            return new FieldPair(fieldname,cells[fieldIndex]);
        }
    }

    /**
     *
     * @param name collect set name
     * @param fieldname field name
     * @return value
     */
    public Object value(final String name,final String fieldname){
        FieldPair field = field(name, fieldname);
        return field!=null?field.first():null;
    }

    /**
     * get sub row
     * @param name collect name
     * @return rowstore list
     */
    @SuppressWarnings("unchecked")
    public Collection<RowStore> rows(final String name){
        Integer collectIndex = information.index(name);
        ParameterUtils.notNull(collectIndex);
        Object o = rowFull[collectIndex];
        if(o == null || !(o instanceof Collection)){
            return null;
        }
        return (Collection<RowStore>) o;
    }

    public boolean isSmallRow(){
        return StringUtils.isNotBlank(collectSetName);
    }

}
