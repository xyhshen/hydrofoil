package org.hydrofoil.common.util.collect;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.hydrofoil.common.util.ParameterUtils;

import java.io.Serializable;
import java.util.*;

import static org.hydrofoil.common.util.ParameterUtils.checkSupport;

/**
 * ArraySet
 * <p>
 * package org.hydrofoil.common.util.collect
 *
 * @author xie_yh
 * @date 2018/7/31 10:55
 */
public class ArraySet<E extends Comparable<? super E>> implements Set<E>, Cloneable,Serializable {

    /**
     * key Storager array
     */
    private Object[] keyStorager;

    public ArraySet(final Collection<E> c){
        init(c);
    }

    private void init(final Collection<? extends E> c){
        ParameterUtils.mustTrueMessage(CollectionUtils.isNotEmpty(c),"original collect is empty");
        keyStorager = c.stream().sorted().toArray(Object[]::new);
    }

    @Override
    public int size() {
        return ArrayUtils.getLength(keyStorager);
    }

    @Override
    public boolean isEmpty() {
        return ArrayUtils.isEmpty(keyStorager);
    }

    private int findIndex(E key){
        if(isEmpty() || key == null){
            return -1;
        }
        return Arrays.binarySearch(keyStorager,key);
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean contains(Object o) {
        return !isEmpty() && findIndex((E) o) != -1;
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {

            private int index = 0;

            @Override
            public boolean hasNext() {
                return index < size();
            }

            @SuppressWarnings("unchecked")
            @Override
            public E next() {
                if(index >= size()){
                    return null;
                }
                return (E) keyStorager[index++];
            }
        };
    }

    @Override
    public Object[] toArray() {
        if(isEmpty()){
            return new Object[0];
        }
        return Arrays.copyOf(keyStorager,size());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T[] toArray(T[] a) {
        if(isEmpty()){
            return a;
        }
        if (a.length < size()){
            // Make a new array of a's runtime type, but my contents:
            return (T[]) Arrays.copyOf(keyStorager, size(), a.getClass());
        }
        System.arraycopy(keyStorager, 0, a, 0, size());
        if (a.length > size()){
            a[size()] = null;
        }
        return a;
    }

    @Override
    public boolean add(E e) {
        checkSupport(false,"can't be add!");
        return false;
    }

    @Override
    public boolean remove(Object o) {
        checkSupport(false,"can't be deleted!");
        return false;
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean containsAll(Collection<?> c) {
        return !isEmpty() && c.stream().filter((v) -> findIndex((E) v) != -1).count() == c.size();
    }

    @Override
    public boolean addAll(Collection<? extends E> c) {
        init(c);
        return true;
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        return false;
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        checkSupport(false,"can't be deleted!");
        return false;
    }

    @Override
    public void clear() {
        keyStorager = null;
    }

    @Override
    public Object clone(){
        try {
            ArraySet set = (ArraySet) super.clone();
            if(!isEmpty()){
                set.keyStorager = Arrays.copyOf(keyStorager,size());
            }
            return set;
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public int hashCode(){
        return Objects.hash((Object[]) keyStorager);
    }

    @Override
    public boolean equals(Object otherObj){
        if(!(otherObj instanceof ArraySet)){
            return false;
        }
        return Arrays.equals(((ArraySet)otherObj).keyStorager,keyStorager);
    }
}
