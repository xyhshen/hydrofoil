package org.hydrofoil.common.util;

import org.apache.commons.lang3.tuple.Pair;

import java.util.Arrays;

/**
 * Algorithms
 * <p>
 * package org.hydrofoil.common.util
 *
 * @author xie_yh
 * @date 2018/11/6 18:55
 */
public final class Algorithms {

    private Algorithms(){}

    public static int similarSearch(Object[] a,Object v){
        int low = 0;
        int high = a.length - 1;

        int last = 0;

        while (low <= high) {
            int mid = (low + high) >>> 1;
            @SuppressWarnings("rawtypes")
            Comparable midVal = (Comparable)a[mid];
            @SuppressWarnings("unchecked")
            int cmp = midVal.compareTo(v);

            if (cmp < 0){
                low = mid + 1;
            }else if (cmp > 0){
                high = mid - 1;
            }else{
                return mid; // key found
            }
            last = mid;
        }
        return last;  // key not found.
    }

    public static Pair<Integer,Integer> searchRanger(final Object[] a, final Object start, final Object end){
        int fromIndex = similarSearch(a,start);
        int toIndex = a.length - 1;
        if(end != null){
            toIndex = Arrays.binarySearch(a,end);
            if(toIndex < 0){
                return null;
            }
        }

        return Pair.of(fromIndex,toIndex);
    }
}
