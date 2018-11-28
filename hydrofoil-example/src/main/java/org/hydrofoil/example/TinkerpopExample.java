package org.hydrofoil.example;

import org.apache.commons.collections4.IteratorUtils;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.apache.tinkerpop.gremlin.structure.VertexProperty;
import org.hydrofoil.common.configuration.HydrofoilConfiguration;
import org.hydrofoil.core.HydrofoilFactory;
import org.hydrofoil.core.tinkerpop.structure.HydrofoilTinkerpopGraph;

import java.util.Iterator;
import java.util.List;

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
        try(HydrofoilTinkerpopGraph graph = HydrofoilFactory.open(configuration)){
            final GraphTraversalSource g = graph.traversal();
            final List<Vertex> vertices = g.V().hasLabel("team").limit(10).toList();
            vertices.forEach(v->{
                final VertexProperty<Object> coaches = v.property("coaches");
                final Iterator<VertexProperty<Object>> stars = v.properties("star");
                IteratorUtils.toList(stars).forEach(star->{
                    System.out.println("star:" + star.value());
                });
                System.out.println(coaches.value());
                System.out.println(coaches.value("chief.coach").toString());
                System.out.println(coaches.value("assistant.coach").toString());
                System.out.println(coaches.value("fitness.coach").toString());
                System.out.println(coaches.value("goalkeeper.coach").toString());
            });
        }
    }
}
