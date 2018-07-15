package org.hydrofoil.example;

import org.hydrofoil.common.configuration.HydrofoilConfiguration;
import org.hydrofoil.common.configuration.HydrofoilConfigurationProperties;
import org.hydrofoil.common.graph.GraphVertexId;
import org.hydrofoil.core.HydrofoilConnector;
import org.hydrofoil.core.HydrofoilFactory;
import org.hydrofoil.core.standard.StandardVertex;

import java.util.Iterator;

/**
 * HydrofoilExample
 * <p>
 * package org.hydrofoil.example
 *
 * @author xie_yh
 * @date 2018/7/14 20:31
 */
public final class HydrofoilExample {

    public static void main(String[] args) throws Exception {
        HydrofoilConfiguration configuration = new HydrofoilConfiguration();
        configuration.putSchemaFile(HydrofoilConfigurationProperties.SCHEMA_DATASOURCE,"datasource.xml");
        configuration.putSchemaFile(HydrofoilConfigurationProperties.SCHEMA_DATASET,"dataset.xml");
        configuration.putSchemaFile(HydrofoilConfigurationProperties.SCHEMA_MAPPER,"mapper.xml");
        try(HydrofoilConnector connector = HydrofoilFactory.connect(configuration)){
            Iterator<StandardVertex> take = connector.vertices(new GraphVertexId("person").unique("idnumber","370601198205043112")).take();
            System.out.println(take);
        }

    }

}
