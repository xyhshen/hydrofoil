package org.hydrofoil.core.engine.internal;

import org.apache.commons.collections4.KeyValue;
import org.apache.commons.collections4.MultiMapUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.keyvalue.DefaultKeyValue;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.mutable.MutableObject;
import org.hydrofoil.common.provider.IDataConnector;
import org.hydrofoil.common.provider.datasource.BaseRowQuery;
import org.hydrofoil.common.provider.datasource.RowQueryResponse;
import org.hydrofoil.common.provider.datasource.RowQueryScan;
import org.hydrofoil.common.provider.datasource.response.RowCountResponse;
import org.hydrofoil.common.util.DataUtils;
import org.hydrofoil.common.util.LogUtils;
import org.hydrofoil.common.util.ArgumentUtils;
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

    private static final Logger logger = LogUtils.getLogger(ConnectRunner.class);

    private final Management management;

    private List<E> elements = new ArrayList<>(100);

    private String groupFieldName;

    private final boolean returnCount;

    private BiFunction<ElementMapping,RowQueryResponse,Collection<E>> handleFunction;

    ConnectRunner(final Management management,final BiFunction<ElementMapping,RowQueryResponse,Collection<E>> handleFunction){
        this.management = management;
        this.handleFunction = handleFunction;
        this.returnCount = false;
    }

    ConnectRunner(final Management management,final String groupFieldName){
        this.management = management;
        this.returnCount = true;
        this.groupFieldName = groupFieldName;
        this.handleFunction = this::doHandelCountRequest;
    }

    private static boolean isGet(final Collection<BaseRowQuery> queries){
        return queries.stream().filter(p->p instanceof RowQueryScan).count() == 0;
    }

    @SuppressWarnings("unchecked")
    private Collection<E> doHandelCountRequest(final ElementMapping elementMapping,
                                               final RowQueryResponse rowQueryResponse){
        ArgumentUtils.mustTrue(rowQueryResponse instanceof RowCountResponse);
        RowCountResponse countResponse = (RowCountResponse) rowQueryResponse;
        ArgumentUtils.mustTrueException(countResponse.isSucceed(),"count failed",countResponse.getException());
        Collection<KeyValue<?,Long>> counts;
        if(StringUtils.isNotBlank(groupFieldName)){
            counts = new ArrayList<>(rowQueryResponse.counts());
        }else{
            counts = Collections.singleton((KeyValue<?, Long>) new DefaultKeyValue("",countResponse.count()));
        }
        return (Collection<E>) counts;
    }

    boolean select(final MultiValuedMap<String, ElementMapping> maps){
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
                if(returnCount){
                    rowQueryResponseIterator = datasource.countRow(rowQueryRequests,groupFieldName);
                }else{
                    if(getQuery){
                        rowQueryResponseIterator = datasource.getRows(DataUtils.castCollectType(rowQueryRequests));
                    }else{
                        rowQueryResponseIterator = datasource.scanRow(DataUtils.castCollectType(rowQueryRequests));
                    }
                }
                //process response
                while(rowQueryResponseIterator.hasNext()){
                    RowQueryResponse next = rowQueryResponseIterator.next();
                    ArgumentUtils.mustTrueException(next.isSucceed(),
                            "request datasource failed",
                            next.getException());
                    final ElementMapping elementMapping = elementMappingMap.get(next.id());
                    if(!getQuery &&
                            ((RowQueryScan)elementMapping.getQueryRequest()).isSimpleScan() &&
                            !returnCount){
                        next = nextGetScan(elementMapping,next);
                    }
                    elements.addAll(processResponse(elementMapping,next));
                }
            }
        }catch (Throwable t){
            logger.error("execute select exception",t);
            return false;
        }
        return true;
    }

    private Collection<E> processResponse(final ElementMapping mapping,final RowQueryResponse response){
        if(mapping.getDeriveFunction() == null){
            return handleFunction.apply(mapping,response);
        }
        //derive
        processDeriveRequest(mapping,response);
        return handleFunction.apply(mapping,response);
    }

    @SuppressWarnings("unchecked")
    private RowQueryResponse nextGetScan(final ElementMapping mapping,final RowQueryResponse scanResponse){
        final ElementMapping getElementMapping = mapping.getToGetMappingFunction().apply(mapping, scanResponse);
        final MutableObject mutableObject = new MutableObject();
        ConnectRunner getRunner = new ConnectRunner(management,(m, p)-> {
            mutableObject.setValue(p);
            return Collections.singleton(p);
        });
        MultiValuedMap<String,ElementMapping> mappings = MultiMapUtils.newListValuedHashMap();
        mappings.put(getElementMapping.getDatasource(),getElementMapping);
        ArgumentUtils.mustTrueMessage(getRunner.select(mappings),
                "get query failed");
        return (RowQueryResponse) mutableObject.getValue();
    }

    @SuppressWarnings("unchecked")
    private void processDeriveRequest(final ElementMapping mapping, final RowQueryResponse response){
        final Collection<ElementMapping> deriveMappings = mapping.getDeriveFunction().apply(mapping, response);
        ArgumentUtils.notEmpty(deriveMappings,"derive mapping");
        deriveMappings.forEach(m->m.setBaseMapping(mapping));
        for(ElementMapping deriveMapping:deriveMappings){
            ConnectRunner deriveRunner =  new ConnectRunner(management,mapping.getDeriveHandleFunction());
            MultiValuedMap<String,ElementMapping> mappings = MultiMapUtils.newListValuedHashMap();
            mappings.put(deriveMapping.getDatasource(),deriveMapping);
            ArgumentUtils.mustTrueMessage(deriveRunner.select(mappings),
                    "derive query failed");
        }
    }

    Iterator<E> toIterator(){
        return elements.iterator();
    }

    @SuppressWarnings("unchecked")
    long toLong(){
        long l =  elements.stream().map((kv)->{
            KeyValue<?,Long> count = (KeyValue<?, Long>) kv;
            return count.getValue();
        }).reduce((c1,c2)-> c1 + c2).orElse(0L);
        return l;
    }

}
