package org.hydrofoil.example;

import org.hydrofoil.common.configuration.HydrofoilConfiguration;
import org.hydrofoil.common.graph.EdgeDirection;
import org.hydrofoil.common.graph.GraphElementId;
import org.hydrofoil.core.HydrofoilGraph;
import org.hydrofoil.core.HydrofoilFactory;
import org.hydrofoil.core.engine.EngineEdge;
import org.hydrofoil.core.engine.EngineVertex;

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
        configuration.put("hydrofoil.schema.datasource.resource","datasource.xml");
        configuration.put("hydrofoil.schema.dataset.resource","dataset.xml");
        configuration.put("hydrofoil.schema.mapper.resource","mapper.xml");
        try(HydrofoilGraph connector = HydrofoilFactory.create(configuration)){
            Iterator<EngineVertex> vertexIterator = connector.vertices(GraphElementId.VertexId("person","idnumber","370601198205043112")).take();
            vertexIterator = connector.vertices().label("person").field("name","秦宁谦").length(3L).take();
            Iterator<EngineEdge> edgeIterator = connector.edges().label("employ").vertex(vertexIterator.next()).direction(EdgeDirection.In).take();
            edgeIterator = connector.edges().label("employ").field("workunit","济南金贵隆润滑油有限公司").length(10L).take();
            System.out.println(edgeIterator);
        }

    }

}
