package org.hydrofoil.core.standard.internal;

import org.hydrofoil.core.IGraphContext;
import org.hydrofoil.core.management.Management;
import org.hydrofoil.core.standard.StandardEdge;
import org.hydrofoil.core.standard.StandardVertex;
import org.hydrofoil.core.standard.query.GraphCondition;

import java.io.IOException;
import java.util.Iterator;

/**
 * GraphElementSet
 * <p>
 * package org.hydrofoil.core.internal
 *
 * @author xie_yh
 * @date 2018/7/2 16:00
 */
public class StandardGraphContext implements IGraphContext {

    private Management management;

    public StandardGraphContext(Management management){
        this.management = management;
    }

    @Override
    public Iterator<StandardVertex> listVertices(GraphCondition condition){
        return null;
    }

    @Override
    public Iterator<StandardEdge> listEdges(GraphCondition condition){
        return null;
    }

    @Override
    public void close() throws IOException {

    }
}
