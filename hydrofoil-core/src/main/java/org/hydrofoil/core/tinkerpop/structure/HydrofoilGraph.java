package org.hydrofoil.core.tinkerpop.structure;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.tinkerpop.gremlin.process.computer.GraphComputer;
import org.apache.tinkerpop.gremlin.process.traversal.TraversalStrategies;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.core.HydrofoilConnector;
import org.hydrofoil.core.tinkerpop.glue.IdManage;
import org.hydrofoil.core.tinkerpop.glue.TinkerpopGraphUtils;
import org.hydrofoil.core.tinkerpop.process.traversal.strategy.optimization.HydrofoilGraphStepStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/**
 * HydrofoilGraph
 * <p>
 * package org.hydrofoil.core.graph
 *
 * @author xie_yh
 * @date 2018/7/2 14:14
 */
@Graph.OptIn(Graph.OptIn.SUITE_STRUCTURE_STANDARD)
@Graph.OptIn(Graph.OptIn.SUITE_STRUCTURE_INTEGRATE)
@Graph.OptIn(Graph.OptIn.SUITE_PROCESS_STANDARD)
public final class HydrofoilGraph implements Graph {

    private static Logger logger = LoggerFactory.getLogger(HydrofoilGraph.class);

    /**
     * hydrofoil graph connector
     */
    private final HydrofoilConnector connector;

    /**
     * id manager
     */
    private final IdManage idManage;

    static{
        TraversalStrategies.GlobalCache.registerStrategies(HydrofoilGraph.class,
                TraversalStrategies.GlobalCache.getStrategies(Graph.class).clone().addStrategies(
                        HydrofoilGraphStepStrategy.instance()
                ));
    }

    public HydrofoilGraph(HydrofoilConnector connector){
        this.connector = connector;
        this.idManage = new IdManage(connector.getManagement().getSchemaManager());
    }

    /**
     * get Hydrofoil connector
     * @return connector
     */
    public HydrofoilConnector connector(){
        return connector;
    }

    /**
     * @return IdManage
     * @see HydrofoilGraph#idManage
     **/
    public IdManage getIdManage() {
        return idManage;
    }

    /**
     * @return HydrofoilConnector
     * @see HydrofoilGraph#connector
     **/
    public HydrofoilConnector getConnector() {
        return connector;
    }

    @Override
    public Vertex addVertex(Object... keyValues) {
        return (Vertex) Exceptions.vertexAdditionsNotSupported();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C extends GraphComputer> C compute(Class<C> graphComputerClass) throws IllegalArgumentException {
        return (C) Exceptions.graphDoesNotSupportProvidedGraphComputer(graphComputerClass);
    }

    @Override
    public GraphComputer compute() throws IllegalArgumentException {
        return (GraphComputer) Exceptions.graphComputerNotSupported();
    }

    @Override
    public Iterator<Vertex> vertices(Object... vertexIds) {
        ParameterUtils.mustTrueMessage(ArrayUtils.isNotEmpty(vertexIds),"vertex id's not empty");
        return TinkerpopGraphUtils.listVertexByIds(this,vertexIds);
    }

    @Override
    public Iterator<Edge> edges(Object... edgeIds) {
        ParameterUtils.mustTrueMessage(ArrayUtils.isNotEmpty(edgeIds),"edge id's not empty");
        return TinkerpopGraphUtils.listEdgeByIds(this,edgeIds);
    }

    @Override
    public Transaction tx() {
        return (Transaction) Exceptions.transactionsNotSupported();
    }

    @Override
    public void close() throws Exception {
        IOUtils.closeQuietly(connector);
    }

    @Override
    public Variables variables() {
        return null;
    }

    @Override
    public Configuration configuration() {
        return null;
    }
}
