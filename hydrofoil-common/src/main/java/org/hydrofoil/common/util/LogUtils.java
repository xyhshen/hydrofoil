package org.hydrofoil.common.util;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * LogUtils
 * <p>
 * package org.hydrofoil.common.util
 *
 * @author xie_yh
 * @date 2018/7/11 11:29
 */
public final class LogUtils {

    /**
     * get slf4j logger
     * @param clz class
     * @return logger
     */
    public static Logger getLogger(final Class<?> clz){
        return LoggerFactory.getLogger(clz);
    }

    /**
     * print debug log
     * @param logger logger
     * @param s content
     */
    public static void printDebug(Logger logger,String s){
        printDebug(logger,s,null);
    }

    /**
     * print debug log
     * @param logger logger
     * @param s content
     * @param t throw
     */
    public static void printDebug(Logger logger,String s,Throwable t){
        if(logger.isDebugEnabled()){
            logger.debug(s,t);
        }
    }

}
