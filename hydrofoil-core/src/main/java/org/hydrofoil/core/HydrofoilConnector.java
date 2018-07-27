package org.hydrofoil.core;

import org.hydrofoil.common.configuration.HydrofoilConfiguration;
import org.hydrofoil.common.graph.GraphEdgeId;
import org.hydrofoil.common.graph.GraphVertexId;
import org.hydrofoil.core.standard.management.Management;
import org.hydrofoil.core.standard.query.EdgeGraphQueryRunner;
import org.hydrofoil.core.standard.query.VertexGraphQueryRunner;

import java.io.Closeable;
import java.io.IOException;

/**
 * HydrofoilContext
 * <p>
 * package org.hydrofoil.core
 *
 * @author xie_yh
 * @date 2018/7/2 15:17
 */
public final class HydrofoilConnector implements Closeable,AutoCloseable {

    /**
     * data management
     */
    private Management management;

    HydrofoilConnector(){
        this.management = new Management();
    }

    void init(HydrofoilConfiguration configuration) throws Exception {
        management.load(configuration);
    }

    /**
     * vertex query
     * @param vertexIds ids
     * @return query runner
     */
    public VertexGraphQueryRunner vertices(GraphVertexId ...vertexIds){
        return new VertexGraphQueryRunner(management).elements(vertexIds);
    }

    /**
     * edge query
     * @param edgeIds ids
     * @return query runner
     */
    public EdgeGraphQueryRunner edges(GraphEdgeId ...edgeIds){
        return new EdgeGraphQueryRunner(management).elements(edgeIds);
    }

    /**
     * @return Management
     * @see HydrofoilConnector#management
     **/
    public Management getManagement() {
        return management;
    }

    @Override
    public void close() throws IOException {
        management.close();
    }
}
