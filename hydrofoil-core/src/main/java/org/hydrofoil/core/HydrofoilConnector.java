package org.hydrofoil.core;

import org.hydrofoil.core.management.Management;
import org.hydrofoil.core.standard.internal.StandardGraphContext;

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
     * data management
     */
    private Management management;

    HydrofoilConnector(){}

    void init(){
    }

    /**
     * get graph object context
     * @return graph object context
     */
    public IGraphContext graph(){
        return new StandardGraphContext(management);
    }


    @Override
    public void close() throws IOException {

    }
}
