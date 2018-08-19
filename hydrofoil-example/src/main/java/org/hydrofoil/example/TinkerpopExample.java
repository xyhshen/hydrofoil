package org.hydrofoil.example;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.apache.tinkerpop.gremlin.structure.Property;
import org.apache.tinkerpop.gremlin.structure.Vertex;
import org.hydrofoil.common.configuration.HydrofoilConfiguration;
import org.hydrofoil.core.HydrofoilFactory;
import org.hydrofoil.core.tinkerpop.structure.HydrofoilGraph;
import org.hydrofoil.tinkerpop.EP;

import java.util.List;
import java.util.Map;

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
            final GraphTraversalSource g = graph.traversal();
            List<Vertex> vertices = g.V().hasLabel("person").
                    has("name", EP.like("张")).limit(10).toList();
            vertices.stream().forEach(v->{
                System.out.println((String)v.value("name"));
                System.out.println((String)v.value("idnumber"));
                System.out.println((String)v.value("gender"));
                System.out.println((String)v.value("address"));
                System.out.println((String)v.value("workunit"));
            });
            List<? extends Property<Object>> properties = g.V().hasLabel("person").
                    has("idnumber", "371102198509180158").properties().toList();
            System.out.println(properties);
            final List<Map<Object, Object>> maps = g.V().hasLabel("person").
                    has("name", EP.like("钱")).limit(10).in().valueMap(false).toList();
            System.out.println(maps);
        }
    }
}
