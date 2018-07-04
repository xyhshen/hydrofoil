package org.hydrofoil.core.standard.internal;

import org.hydrofoil.core.management.DataManager;
import org.hydrofoil.core.management.SchemaManager;
import org.hydrofoil.core.standard.StandardEdge;
import org.hydrofoil.core.standard.StandardVertex;
import org.hydrofoil.core.standard.query.GraphCondition;

import java.util.Collection;
import java.util.Iterator;

/**
 * GraphElementSet
 * <p>
 * package org.hydrofoil.core.internal
 *
 * @author xie_yh
 * @date 2018/7/2 16:00
 */
public abstract class AbstractGraphQueryRunner {

    protected AbstractGraphQueryRunner(){}

    protected abstract DataManager getDataManager();

    protected abstract SchemaManager getSchemaManager();

    public Iterator<StandardVertex> listVertex(GraphCondition condition){
        return null;
    }

    public Iterator<StandardEdge> listEdge(GraphCondition condition){
        return null;
    }

}