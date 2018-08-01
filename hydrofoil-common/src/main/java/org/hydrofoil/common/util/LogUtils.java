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
    public static void printDebug(final Logger logger,final String s){
        printDebug(logger,s,null);
    }

    /**
     * print debug log
     * @param logger logger
     * @param s content
     * @param t throw
     */
    public static void printDebug(final Logger logger,final String s,Throwable t){
        if(logger.isDebugEnabled()){
            logger.debug(s,t);
        }
    }

    public static void callError(final Logger logger,final String methodName){
        callError(logger,methodName,null);
    }

    public static void callError(final Logger logger,final String methodName,Throwable t){
        StringBuilder sb = new StringBuilder();
        sb.append("call method \"").append(methodName).append("\"").append(" failed");
        if(t != null){
            logger.error(sb.toString(),t);
        }else{
            logger.error(sb.toString());
        }
    }

}
