package org.hydrofoil.common.schema;

import org.dom4j.Element;
import org.hydrofoil.common.util.ArgumentUtils;
import org.hydrofoil.common.util.XmlUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * EdgeSchema
 * <p>
 * package org.hydrofoil.common.schema
 *
 * @author xie_yh
 * @date 2018/7/3 16:22
 */
public final class EdgeSchema extends BaseElementSchema {

    private static final String NODE_EDGE_SOURCE_ELEMENT = "source";
    private static final String NODE_EDGE_TARGET_ELEMENT = "target";

    private static final String NODE_EDGE_CONNECTION_ELEMENT = "connection";

    private static final String ATTR_EDGE_CONNECTION_VERTEX = "vertex";
    private static final String ATTR_EDGE_CONNECTION_EDGE = "edge";

    private static final String ATTR_EDGE_VERTEX_LABEL = "label";
    private static final String ATTR_EDGE_VERTEX_PROPERTY = "property";

    public static class EdgeConnection{

        private final String vertexPropertyLabel;

        private final String edgePropertyLabel;

        private EdgeConnection(final String vertexPropertyLabel,final String edgePropertyLabel){
            this.vertexPropertyLabel = vertexPropertyLabel;
            this.edgePropertyLabel = edgePropertyLabel;
        }

        /**
         * @return String
         * @see EdgeConnection#vertexPropertyLabel
         **/
        public String getVertexPropertyLabel() {
            return vertexPropertyLabel;
        }

        /**
         * @return String
         * @see EdgeConnection#edgePropertyLabel
         **/
        public String getEdgePropertyLabel() {
            return edgePropertyLabel;
        }
    }

    private Collection<EdgeConnection> sourceConnections;

    private Collection<EdgeConnection> targetConnections;

    public EdgeSchema(){}

    @Override
    void parse(final Element node){
        super.parse(node);
        //parse edge info
        Element source = node.element(NODE_EDGE_SOURCE_ELEMENT);
        Element target = node.element(NODE_EDGE_TARGET_ELEMENT);

        ArgumentUtils.notNull(source);
        ArgumentUtils.notNull(target);

        String sourceLabel = XmlUtils.attributeStringValue(source,ATTR_EDGE_VERTEX_LABEL);
        String targetLabel = XmlUtils.attributeStringValue(target,ATTR_EDGE_VERTEX_LABEL);;

        ArgumentUtils.notBlank(sourceLabel);
        ArgumentUtils.notBlank(targetLabel);

        putItem("source-" + ATTR_EDGE_VERTEX_LABEL,sourceLabel);
        sourceConnections = loadConnection(source);
        putItem("target-" + ATTR_EDGE_VERTEX_LABEL,targetLabel);
        targetConnections = loadConnection(target);
    }

    private Collection<EdgeConnection> loadConnection(final Element node){
        final List<Element> elements = XmlUtils.listElement(node, NODE_EDGE_CONNECTION_ELEMENT);
        Collection<EdgeConnection> connections = new ArrayList<>(elements.size());
        for(Element element:elements){
            String vertexPropertyLabel = ArgumentUtils.notBlank(XmlUtils.attributeStringValue(element,ATTR_EDGE_CONNECTION_VERTEX));
            String edgePropertyLabel = ArgumentUtils.notBlank(XmlUtils.attributeStringValue(element,ATTR_EDGE_CONNECTION_EDGE));
            connections.add(new EdgeConnection(vertexPropertyLabel,edgePropertyLabel));
        }
        return connections;
    }

    /**
     * get source vertex label
     * @return label
     */
    public String getSourceLabel(){
        return getItem("source-" + ATTR_EDGE_VERTEX_LABEL);
    }

    /**
     * get source vertex field
     * @return k,v->(vertex property label,table field name)
     */
    public Collection<EdgeConnection> getSourceConnections(){
        return sourceConnections;
    }

    /**
     * get target vertex label
     * @return label
     */
    public String getTargetLabel(){
        return getItem("target-" + ATTR_EDGE_VERTEX_LABEL);
    }

    /**
     * get target vertex field
     * @return k,v->(vertex property label,table field name)
     */
    public Collection<EdgeConnection> getTargetConnections(){
        return targetConnections;
    }

}
