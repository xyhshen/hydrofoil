package org.hydrofoil.core.tinkerpop.structure;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.MapConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.tinkerpop.gremlin.process.computer.GraphComputer;
import org.apache.tinkerpop.gremlin.process.traversal.TraversalStrategies;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.core.HydrofoilGraph;
import org.hydrofoil.core.tinkerpop.glue.IdManage;
import org.hydrofoil.core.tinkerpop.glue.TinkerpopGraphTransit;
import org.hydrofoil.core.tinkerpop.process.traversal.strategy.optimization.HydrofoilGraphStepStrategy;
import org.hydrofoil.core.tinkerpop.process.traversal.strategy.optimization.HydrofoilVertexStepStrategy;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;

/**
 * HydrofoilTinkerpopGraph
 * <p>
 * package org.hydrofoil.core.graph
 *
 * @author xie_yh
 * @date 2018/7/2 14:14
 */
@Graph.OptIn(Graph.OptIn.SUITE_STRUCTURE_STANDARD)
@Graph.OptIn(Graph.OptIn.SUITE_STRUCTURE_INTEGRATE)
@Graph.OptIn(Graph.OptIn.SUITE_PROCESS_STANDARD)
public final class HydrofoilTinkerpopGraph implements Graph {

    private static Logger logger = LoggerFactory.getLogger(HydrofoilTinkerpopGraph.class);

    /**
     * hydrofoil graph connector
     */
    private final HydrofoilGraph connector;

    /**
     * id manager
     */
    private final IdManage idManage;

    static{
        TraversalStrategies.GlobalCache.registerStrategies(HydrofoilTinkerpopGraph.class,
                TraversalStrategies.GlobalCache.getStrategies(Graph.class).clone().addStrategies(
                        HydrofoilGraphStepStrategy.instance(),
                        HydrofoilVertexStepStrategy.instance()
                ));
    }

    public HydrofoilTinkerpopGraph(HydrofoilGraph connector){
        this.connector = connector;
        this.idManage = new IdManage(connector.getManagement().getSchemaManager());
    }

    /**
     * get Hydrofoil connector
     * @return connector
     */
    public HydrofoilGraph connector(){
        return connector;
    }

    /**
     * @return IdManage
     * @see HydrofoilTinkerpopGraph#idManage
     **/
    public IdManage getIdManage() {
        return idManage;
    }

    /**
     * @return HydrofoilGraph
     * @see HydrofoilTinkerpopGraph#connector
     **/
    public HydrofoilGraph getConnector() {
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
        return TinkerpopGraphTransit.of(this).listVerticesByIds(vertexIds);
    }

    @Override
    public Iterator<Edge> edges(Object... edgeIds) {
        ParameterUtils.mustTrueMessage(ArrayUtils.isNotEmpty(edgeIds),"edge id's not empty");
        return TinkerpopGraphTransit.of(this).listEdgesByIds(edgeIds);
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
        return new MapConfiguration(connector.getConfiguration().toMap());
    }
}
