package org.hydrofoil.example;

import org.apache.tinkerpop.gremlin.structure.Direction;
import org.apache.tinkerpop.gremlin.structure.Edge;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.hydrofoil.common.configuration.HydrofoilConfiguration;
import org.hydrofoil.common.graph.GraphElementId;
import org.hydrofoil.core.HydrofoilFactory;
import org.hydrofoil.core.tinkerpop.structure.HydrofoilGraph;

import java.util.Iterator;
import java.util.Optional;

/**
 * TinkerpopExample
 * <p>
 * package org.hydrofoil.example
 *
 * @author xie_yh
 * @date 2018/7/24 16:50
 */
public final class TinkerpopExample {

    public static void main(String[] args) throws Exception {
        HydrofoilConfiguration configuration = new HydrofoilConfiguration();
        configuration.put("hydrofoil.schema.datasource.resource","datasource.xml");
        configuration.put("hydrofoil.schema.dataset.resource","dataset.xml");
        configuration.put("hydrofoil.schema.mapper.resource","mapper.xml");
        try(HydrofoilGraph graph = HydrofoilFactory.openTinkerpop(configuration)){
            Optional<Vertex> optional = graph.traversal().V(GraphElementId.VertexId("person","idnumber", "370601198205043112")).tryNext();
            Vertex vertex = optional.orElseGet(null);
            Iterator<Edge> employ = vertex.edges(Direction.BOTH, "employ");
            Iterator<Vertex> vertices = vertex.vertices(Direction.BOTH, "employ");
            vertices.forEachRemaining((v)->{
                System.out.println(v.id());
                System.out.println(v.label());
                System.out.println(v.property("name").value());
            });
            System.out.println(vertex);
            /*graph.traversal().E().hasId(P.eq(0)).match(__.as("aa2").has("ccc")).V().limit(100).tryNext();
            graph.traversal().V().hasId(1).properties("aaa").as("ss").tryNext();
            graph.traversal().V().hasLabel("person").has("idnumber","3710382323").inE("ss").count().tryNext();
            graph.traversal().V(1).tryNext();*/
        }
    }
}
