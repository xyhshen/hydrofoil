package org.hydrofoil.core.engine.internal;

import org.apache.commons.collections4.MultiValuedMap;
import org.hydrofoil.common.provider.IDataConnector;
import org.hydrofoil.common.provider.datasource.BaseRowQuery;
import org.hydrofoil.common.provider.datasource.RowQueryResponse;
import org.hydrofoil.common.provider.datasource.RowQueryScan;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.common.util.LogUtils;
import org.hydrofoil.core.engine.management.Management;
import org.slf4j.Logger;

import java.util.*;
import java.util.function.BiFunction;

/**
 * ConnectRunner
 * <p>
 * package org.hydrofoil.core.engine.internal
 *
 * @author xie_yh
 * @date 2018/11/22 18:55
 */
public final class ConnectRunner<E> {

    private final Management management;

    private List<E> elements;

    private BiFunction<ElementMapping,RowQueryResponse,Collection<E>> handleFunction;

    private static final Logger logger = LogUtils.getLogger(ConnectRunner.class);

    ConnectRunner(final Management management,final BiFunction<ElementMapping,RowQueryResponse,Collection<E>> handleFunction){
        this.management = management;
        this.elements = new ArrayList<>(100);
        this.handleFunction = handleFunction;
    }

    private static boolean isGet(final Collection<BaseRowQuery> queries){
        return queries.stream().filter(p->p instanceof RowQueryScan).count() == 0;
    }

    boolean list(MultiValuedMap<String,ElementMapping> maps){
        try{
            for(String datasourceName:maps.keySet()){
                final Collection<ElementMapping> elementMappings = maps.get(datasourceName);
                IDataConnector datasource = management.getDataSourceManager().
                        getDatasource(datasourceName);
                Map<Long,ElementMapping> elementMappingMap = new HashMap<>();
                List<BaseRowQuery> rowQueryRequests = new ArrayList<>(elementMappings.size());
                elementMappings.forEach((request)->{
                    elementMappingMap.put(request.getQueryRequest().getId(),request);
                    rowQueryRequests.add(request.getQueryRequest());
                });
                boolean getQuery = isGet(rowQueryRequests);
                Iterator<RowQueryResponse> rowQueryResponseIterator;
                if(getQuery){
                    rowQueryResponseIterator = datasource.getRows(DataUtils.castCollectType(rowQueryRequests));
                }else{
                    rowQueryResponseIterator = datasource.scanRow(DataUtils.castCollectType(rowQueryRequests));
                }

                while(rowQueryResponseIterator.hasNext()){
                    RowQueryResponse next = rowQueryResponseIterator.next();
                    elements.addAll(handleFunction.apply(elementMappingMap.get(next.id()),
                            next));
                }
            }
        }catch (Throwable t){
            logger.error("execute list exception",t);
            return false;
        }
        return true;
    }

    Iterator<E> toIterator(){
        return elements.iterator();
    }

}
