package org.hydrofoil.core.tinkerpop;

import org.apache.commons.configuration.Configuration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.tinkerpop.gremlin.process.computer.GraphComputer;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Graph;
import org.apache.tinkerpop.gremlin.structure.Transaction;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.hydrofoil.common.util.ParameterUtils;
import org.hydrofoil.core.HydrofoilConnector;

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

    private HydrofoilConnector connector;

    public HydrofoilGraph(HydrofoilConnector connector){
        this.connector = connector;
    }

    /**
     * get Hydrofoil connector
     * @return connector
     */
    public HydrofoilConnector connector(){
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
        ParameterUtils.mustTrueMessage(ArrayUtils.isNotEmpty(vertexIds),"ids not empty");
        return null;
    }

    @Override
    public Iterator<Edge> edges(Object... edgeIds) {
        ParameterUtils.mustTrueMessage(ArrayUtils.isNotEmpty(edgeIds),"ids not empty");
        return null;
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
