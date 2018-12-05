package org.hydrofoil.common.provider.datasource.response;

import org.apache.commons.collections4.KeyValue;
import org.apache.commons.collections4.keyvalue.DefaultKeyValue;
import org.apache.commons.lang3.RandomUtils;
import org.hydrofoil.common.provider.datasource.RowStore;
import org.hydrofoil.common.util.DataUtils;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * RowCountResponse
 * <p>
 * package org.hydrofoil.common.provider.datasource
 *
 * @author xie_yh
 * @date 2018/11/15 19:02
 */
public class RowCountResponse extends AbstractRowQueryResponse {

    private Map<String,Long> countMap;

    public RowCountResponse(Long id,Long count) {
        super(id,true);
        this.countMap = Collections.singletonMap(RandomUtils.nextLong() + "***key",count);
    }

    public RowCountResponse(Long id,Map<?,Long> count) {
        super(id,true);
        this.countMap = DataUtils.newMapWithMaxSize(10);
    }

    @Override
    public Iterable<RowStore> getRows() {
        return null;
    }

    @Override
    public Long count() {
        if(countMap.isEmpty()){
            return null;
        }
        return DataUtils.collectFirst(countMap.values());
    }

    @Override
    public Collection<KeyValue<?,Long>> counts(){
        return countMap.entrySet().stream().map(e-> new DefaultKeyValue<String,Long>(e.getKey(),e.getValue())).
                collect(Collectors.toList());
    }
}
