package org.hydrofoil.example;

import org.apache.tinkerpop.gremlin.process.traversal.P;
import org.apache.tinkerpop.gremlin.process.traversal.dsl.graph.__;
import org.hydrofoil.common.configuration.HydrofoilConfiguration;
import org.hydrofoil.common.configuration.HydrofoilConfigurationProperties;
import org.hydrofoil.core.HydrofoilFactory;
import org.hydrofoil.core.tinkerpop.structure.HydrofoilGraph;

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
            graph.traversal().E().hasId(P.eq(0)).match(__.as("aa2").has("ccc")).V().limit(100).tryNext();
            graph.traversal().V().hasId(1).properties("aaa").as("ss").tryNext();
            graph.traversal().V().hasLabel("person").has("idnumber","3710382323").inE("ss").count().tryNext();
            graph.traversal().V(1).tryNext();
        }
    }
}
