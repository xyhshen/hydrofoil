package org.hydrofoil.core.standard.internal;

import org.apache.commons.collections4.CollectionUtils;
import org.hydrofoil.common.graph.GraphVertexId;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.core.IGraphContext;
import org.hydrofoil.core.management.Management;
import org.hydrofoil.core.standard.StandardEdge;
import org.hydrofoil.core.standard.StandardVertex;
import org.hydrofoil.core.standard.query.EdgeGraphCondition;
import org.hydrofoil.core.standard.query.VertexGraphCondition;

import java.io.IOException;
import java.util.Iterator;

/**
 * GraphElementSet
 * <p>
 * package org.hydrofoil.core.internal
 *
 * @author xie_yh
 * @date 2018/7/2 16:00
 */
public class StandardGraphContext implements IGraphContext {

    private Management management;

    public StandardGraphContext(Management management){
        this.management = management;
    }

    @SuppressWarnings("SuspiciousToArrayCall")
    @Override
    public Iterator<StandardVertex> listVertices(VertexGraphCondition condition){
        VertexMapper vertexMapper = new VertexMapper(management);
        GraphConditionHelper helper = new GraphConditionHelper(management.getSchemaManager());

        if(CollectionUtils.isNotEmpty(condition.ids())){
            /*
            query vertex by id style
             */
            ParameterUtils.mustTrue(helper.checkVertexIds(condition.ids()),"check vertex id");
            vertexMapper.elements(condition.ids().toArray(new GraphVertexId[condition.ids().size()]));
        }else{
            /*
            query vertex by label or other complex style
             */
            ParameterUtils.notBlank(condition.label(),"vertex label");
            vertexMapper.start(condition.start()).limit(condition.limit()).label(condition.label());
        }

        return vertexMapper.list();
    }

    @Override
    public Iterator<StandardEdge> listEdges(EdgeGraphCondition condition){
        return null;
    }

    @Override
    public void close() throws IOException {

    }
}
