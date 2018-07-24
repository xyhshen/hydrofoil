package org.hydrofoil.example;

import org.hydrofoil.common.configuration.HydrofoilConfiguration;
import org.hydrofoil.common.configuration.HydrofoilConfigurationProperties;
import org.hydrofoil.core.HydrofoilFactory;
import org.hydrofoil.core.tinkerpop.HydrofoilGraph;

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
        configuration.putSchemaFile(HydrofoilConfigurationProperties.SCHEMA_DATASOURCE,"datasource.xml");
        configuration.putSchemaFile(HydrofoilConfigurationProperties.SCHEMA_DATASET,"dataset.xml");
        configuration.putSchemaFile(HydrofoilConfigurationProperties.SCHEMA_MAPPER,"mapper.xml");
        try(HydrofoilGraph graph = HydrofoilFactory.openTinkerpop(configuration)){
            graph.traversal().V(1).tryNext();
        }
    }
}
