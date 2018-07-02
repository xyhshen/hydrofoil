package org.hydrofoil.core;

import org.hydrofoil.core.management.DataManager;
import org.hydrofoil.core.management.SchemaManager;

import java.io.Closeable;
import java.io.IOException;

/**
 * HydrofoilContext
 * <p>
 * package org.hydrofoil.core
 *
 * @author xie_yh
 * @date 2018/7/2 15:17
 */
public final class HydrofoilConnector implements Closeable,AutoCloseable {

    static class HydrofoilConnectorBuilder{

        HydrofoilConnector build(){
            return new HydrofoilConnector();
        }
    }

    /**
     * schema manager
     */
    private SchemaManager schemaManager;

    /**
     * data manager
     */
    private DataManager dataManager;

    HydrofoilConnector(){}

    void init(){
    }

    /**
     * get graph object context
     * @return graph object context
     */
    public StandardGraphContext graph(){
        return new StandardGraphContext(dataManager,schemaManager);
    }


    @Override
    public void close() throws IOException {

    }
}
