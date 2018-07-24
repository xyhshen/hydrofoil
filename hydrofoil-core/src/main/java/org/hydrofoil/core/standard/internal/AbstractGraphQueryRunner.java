package org.hydrofoil.core.standard.internal;

import org.apache.commons.collections4.IterableUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.lang3.ArrayUtils;
import org.hydrofoil.common.graph.GraphElementId;
import org.hydrofoil.common.graph.QMatch;
import org.hydrofoil.common.provider.IDataSource;
import org.hydrofoil.common.provider.datasource.RowQueryRequest;
import org.hydrofoil.common.provider.datasource.RowQueryResponse;
import org.hydrofoil.common.provider.datasource.RowStore;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.core.standard.management.Management;
import org.hydrofoil.core.standard.IGraphQueryRunner;
import org.hydrofoil.core.standard.StandardElement;

import java.util.*;

/**
 * GraphQueryRunner
 * <p>
 * package org.hydrofoil.core.standard.internal
 *
 * @author xie_yh
 * @date 2018/7/14 10:05
 */
@SuppressWarnings("unchecked")
public abstract class AbstractGraphQueryRunner <E extends StandardElement,T extends IGraphQueryRunner> implements IGraphQueryRunner<E,T> {

    protected Management management;

    /**
     * element id
     */
    protected Set<GraphElementId> elementIds;

    /**
     * start
     */
    protected Long start;

    /**
     * limit
     */
    protected Long limit;

    /**
     * label
     */
    protected String label;

    /**
     * query set
     */
    protected Set<QMatch.Q> propertyQuerySet;

    protected AbstractGraphQueryRunner(Management management){
        this.start = null;
        this.limit = null;
        this.propertyQuerySet = new HashSet<>();
        this.elementIds = new HashSet<>();
        this.management = management;
    }

    @Override
    public T start(final Long start){
        this.start = start;
        return (T) this;
    }

    @Override
    public T limit(final Long limit){
        this.limit = limit;
        return (T) this;
    }

    @Override
    public T label(final String label){
        this.label = label;
        return (T) this;
    }

    @Override
    public T fields(final QMatch.Q ...query){
        ParameterUtils.mustTrue(ArrayUtils.isNotEmpty(query),"property query");
        this.propertyQuerySet.addAll(Arrays.asList(query));
        return (T) this;
    }

    @Override
    public T elements(final GraphElementId ...elementIds){
        this.elementIds.addAll(Arrays.asList(elementIds));
        return (T) this;
    }

    /**
     * make query request
     * @return row query request
     */
    protected abstract Collection<ElementMapping> makeQueryRequest();

    /**
     * process ros to element
     * @param rowStore row store
     * @return element
     */
    protected abstract E handleRowToElement(ElementMapping mapping, RowStore rowStore);

    MultiValuedMap<String,ElementMapping> getQueryRequest(){
        MultiValuedMap<String,ElementMapping> maps = new ArrayListValuedHashMap<>();
        Collection<ElementMapping> elementRequests = makeQueryRequest();
        elementRequests.forEach((v)->{
            maps.put(v.getDatasource(),v);
        });
        return  maps;
    }

    Collection<E> handleRowRequest(ElementMapping mapping,RowQueryResponse response){
        ParameterUtils.mustTrueException(response.isSucceed(),
                "request datasource failed",
                response.getException());
        List<RowStore> rowStores = IterableUtils.toList(response);
        List<E> elements = new ArrayList<>(rowStores.size());
        rowStores.forEach((row)->{
            elements.add(handleRowToElement(mapping,row));
        });
        return elements;
    }

    @Override
    public Iterator<E> take() {
        List<E> elements = new ArrayList<>(100);
        MultiValuedMap<String,ElementMapping> maps = getQueryRequest();
        Map<Long,ElementMapping> elementMappingMap = new HashMap<>();
        maps.asMap().forEach((dataSourceName,requests)->{
            IDataSource datasource = management.getDataSourceManager().
                    getDatasource(dataSourceName);
            List<RowQueryRequest> rowQueryRequests = new ArrayList<>(requests.size());
            requests.forEach((request)->{
                elementMappingMap.put(request.getQueryRequest().getId(),request);
                rowQueryRequests.add(request.getQueryRequest());
            });
            Iterator<RowQueryResponse> rowQueryResponseIterator =
                    datasource.sendQuery(rowQueryRequests);
            while(rowQueryResponseIterator.hasNext()){
                RowQueryResponse next = rowQueryResponseIterator.next();
                elements.addAll(handleRowRequest(elementMappingMap.get(next.getId()),
                        next));
            }
        });
        return elements.iterator();
    }

    @Override
    public Long count() {
        return null;
    }


}
