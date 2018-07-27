package org.hydrofoil.core.tinkerpop.glue;

import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.hydrofoil.core.standard.StandardVertex;
import org.hydrofoil.core.tinkerpop.structure.HydrofoilGraph;
import org.hydrofoil.core.tinkerpop.structure.HydrofoilVertex;

import java.util.Iterator;

/**
 * TinkerpopGraphUtils
 * <p>
 * package org.hydrofoil.core.tinkerpop.glue
 *
 * @author xie_yh
 * @date 2018/7/27 18:13
 */
public final class TinkerpopGraphUtils {

    private static Long EMPTY_MAX_LENGTH = 20000L;

    public static Iterator<Vertex> listVertexByIds(HydrofoilGraph graph, Object... vertexIds){
        Iterator<StandardVertex> vertexIterator = graph.getConnector().vertices(graph.getIdManage().vertexIds(vertexIds)).
                length(EMPTY_MAX_LENGTH).take();
        return new Iterator<Vertex>() {
            @Override
            public boolean hasNext() {
                return vertexIterator.hasNext();
            }

            @Override
            public Vertex next() {
                return new HydrofoilVertex(graph,vertexIterator.next());
            }
        };
    }

}
