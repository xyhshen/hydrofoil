package org.hydrofoil.core;

/**
 * HydrofoilFactory
 * <p>
 * package org.hydrofoil.core
 *
 * @author xie_yh
 * @date 2018/7/2 15:17
 */
public final class HydrofoilFactory {

    public static HydrofoilConnector build(){
        HydrofoilConnector.HydrofoilConnectorBuilder builder =
                new HydrofoilConnector.HydrofoilConnectorBuilder();
        return builder.build();
    }

}
