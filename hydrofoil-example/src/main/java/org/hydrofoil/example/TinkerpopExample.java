package org.hydrofoil.example;

import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.GraphTraversalSource;
import org.hydrofoil.common.configuration.HydrofoilConfiguration;
import org.hydrofoil.common.configuration.HydrofoilConfigurationItem;
import org.hydrofoil.core.HydrofoilFactory;
import org.hydrofoil.core.tinkerpop.structure.HydrofoilTinkerpopGraph;

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
        configuration.put(HydrofoilConfigurationItem.GlobalVariables.getFullname() + ".resource","variable.properties");
        configuration.put("hydrofoil.schema.datasource.resource","datasource.xml");
        configuration.put("hydrofoil.schema.dataset.resource","dataset.xml");
        configuration.put("hydrofoil.schema.mapper.resource","mapper.xml");
        try(HydrofoilTinkerpopGraph graph = HydrofoilFactory.open(configuration)){
            final GraphTraversalSource g = graph.traversal();
            final Optional<Long> optional = g.V().hasLabel("team").both("belong.to").count().tryNext();
            System.out.println(optional.get());
        }
    }
}
