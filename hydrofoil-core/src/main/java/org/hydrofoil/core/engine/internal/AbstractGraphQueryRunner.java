package org.hydrofoil.core.engine.internal;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MultiValuedMap;
import org.apache.commons.collections4.multimap.ArrayListValuedHashMap;
import org.apache.commons.lang3.ArrayUtils;
import org.hydrofoil.common.graph.GraphElementId;
import org.hydrofoil.common.graph.QMatch;
import org.hydrofoil.common.provider.IDataConnector;
import org.hydrofoil.common.provider.datasource.RowQueryResponse;
import org.hydrofoil.common.provider.datasource.RowQueryScan;
import org.hydrofoil.common.provider.datasource.RowStore;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.core.engine.IGraphQueryRunner;
import org.hydrofoil.core.engine.management.Management;

import java.util.*;

/**
 * GraphQueryRunner
 * <p>
 * package org.hydrofoil.core.engine.internal
 *
 * @author xie_yh
 * @date 2018/7/14 10:05
 */
@SuppressWarnings("unchecked")
public abstract class AbstractGraphQueryRunner <E,T extends IGraphQueryRunner> implements IGraphQueryRunner<E,T> {

    /**
     * Management
     */
    protected Management management;

    /**
     * edge mapper
     */
    protected final EdgeMapper edgeMapper;

    /**
     * vertex mapper
     */
    protected final VertexMapper vertexMapper;

    /**
     * element id
     */
    protected Set<GraphElementId> elementIds;

    /**
     * offset
     */
    protected Long offset;

    /**
     * length
     */
    protected Long length;

    /**
     * labels
     */
    protected Set<String> labels;

    /**
     * query set
     */
    protected Set<QMatch.Q> propertyQuerySet;

    protected AbstractGraphQueryRunner(Management management){
        this.offset = null;
        this.length = null;
        this.propertyQuerySet = new HashSet<>();
        this.elementIds = new HashSet<>();
        this.labels = new HashSet<>();
        this.management = management;
        this.edgeMapper = new EdgeMapper(management.getSchemaManager());
        this.vertexMapper = new VertexMapper(management.getSchemaManager());
    }

    @Override
    public T offset(final Long offset){
        if(offset == null ||
                offset.equals(0L) ||
                offset < 0){
            this.offset = null;
        }else{
            this.offset = offset;
        }
        return (T) this;
    }

    @Override
    public T length(final Long length){
        if(length == null ||
                length.equals(0L) ||
                length < 0){
            this.length = null;
        }else{
            if(length > Integer.MAX_VALUE){
                this.length = (long) Integer.MAX_VALUE;
            }else{
                this.length = length;
            }
        }
        return (T) this;
    }

    @Override
    public T labels(final String ...labels){
        this.labels.addAll(Arrays.asList(ArrayUtils.nullToEmpty(labels)));
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

    private MultiValuedMap<String,ElementMapping> getQueryRequest(){
        MultiValuedMap<String,ElementMapping> maps = new ArrayListValuedHashMap<>();
        Collection<ElementMapping> elementRequests = makeQueryRequest();
        elementRequests.forEach((v)->{
            maps.put(v.getDatasource(),v);
        });
        return  maps;
    }

    private Collection<E> handleRowRequest(ElementMapping mapping, RowQueryResponse response){
        ParameterUtils.mustTrueException(response.isSucceed(),
                "request datasource failed",
                response.getException());
        Iterable<RowStore> rowStores = response.getRows();
        List<E> elements = new ArrayList<>(0);
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
            IDataConnector datasource = management.getDataSourceManager().
                    getDatasource(dataSourceName);
            List<RowQueryScan> rowQueryRequests = new ArrayList<>(requests.size());
            requests.forEach((request)->{
                elementMappingMap.put(request.getQueryRequest().getId(),request);
                rowQueryRequests.add(request.getQueryRequest());
            });
            Iterator<RowQueryResponse> rowQueryResponseIterator =
                    datasource.scanRow(rowQueryRequests);
            while(rowQueryResponseIterator.hasNext()){
                RowQueryResponse next = rowQueryResponseIterator.next();
                elements.addAll(handleRowRequest(elementMappingMap.get(next.id()),
                        next));
            }
        });
        return elements.iterator();
    }

    @Override
    public Long count() {
        return null;
    }

    @Override
    public boolean operable(OperationType type){
        if(CollectionUtils.isNotEmpty(elementIds)){
            return false;
        }
        if(type == OperationType.paging){
            if(CollectionUtils.size(labels) != 1){
                return false;
            }
            return true;
        }
        if(type == OperationType.count){
            return true;
        }
        if(type == OperationType.order){
            if(CollectionUtils.size(labels) != 1){
                return false;
            }
            return true;
        }
        return false;
    }


}
