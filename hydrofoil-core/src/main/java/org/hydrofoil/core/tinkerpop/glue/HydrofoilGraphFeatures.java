package org.hydrofoil.core.tinkerpop.glue;

import org.apache.tinkerpop.gremlin.structure.Graph;
import org.hydrofoil.core.HydrofoilGraph;

/**
 * HydrofoilGraphFeatures
 * <p>
 * package org.hydrofoil.core.tinkerpop.glue
 *
 * @author xie_yh
 * @date 2019/1/4 18:19
 */
public class HydrofoilGraphFeatures implements Graph.Features {

    private class GraphVertexFeatures implements VertexFeatures{

        @Override
        public boolean supportsAddProperty() {
            return false;
        }

        @Override
        public boolean supportsCustomIds() {
            return true;
        }

        @Override
        public boolean supportsMultiProperties() {
            return true;
        }

        @Override
        public boolean supportsMetaProperties() {
            return true;
        }
    }

    private final HydrofoilGraph graph;

    private final GraphVertexFeatures graphVertexFeatures;

    private HydrofoilGraphFeatures(final HydrofoilGraph graph){
        this.graph = graph;
        this.graphVertexFeatures = new GraphVertexFeatures();
    }

    public static HydrofoilGraphFeatures create(final HydrofoilGraph graph){
        final HydrofoilGraphFeatures features = new HydrofoilGraphFeatures(graph);
        features.init();
        return features;
    }

    private void init(){

    }

    @Override
    public VertexFeatures vertex() {
        return graphVertexFeatures;
    }

    @Override
    public EdgeFeatures edge() {
        return null;
    }

    @Override
    public GraphFeatures graph() {
        return null;
    }
}
