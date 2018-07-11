package org.hydrofoil.common.util;

import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.lang.reflect.Constructor;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;

/**
 * LangUtils
 * <p>
 * package org.hydrofoil.common.util
 *
 * @author xie_yh
 * @date 2018/7/10 13:48
 */
public final class LangUtils {

    /**
     * new instance
     * @param className class name
     * @param params param
     * @return new object
     */
    public static <E> E newInstance(String className,Object... params){
        return newInstance(null,className,params);
    }


    /**
     * new instance
     * @param loader class load
     * @param className class name
     * @param params param
     * @return object
     */
    @SuppressWarnings("unchecked")
    public static <E> E newInstance(ClassLoader loader, String className, Object... params){
        E instance = null;
        try {
			/*
			 * 通过反射找到类型的class
			 */
            Class<?> clazz;
            if(loader == null){
                clazz = Class.forName(className);
            }else{
                clazz = loader.loadClass(className);
            }

            if(null == clazz){
                return null;
            }
            if(ArrayUtils.isEmpty(params)){
                instance = (E) clazz.newInstance();
            }else{
                Class<?>[] clzs = new Class<?>[params.length];
                for(int i = 0;i < params.length;i++){
                    clzs[i] = params[i].getClass();
                }

                //Constructor<?> cons[] = clazz.getConstructors();
                //instance = cons[0].newInstance(params);
                Constructor<?> con = clazz.getConstructor(clzs);
                instance = (E) con.newInstance(params);
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return instance;
    }

    /**
     * new instance
     * @param clz class
     * @param params param
     * @return new object
     */
    @SuppressWarnings("unchecked")
    public static <E> E newInstance(Class<?> clz, Object... params){
        E instance;
        try{
            if(ArrayUtils.isEmpty(params)){
                instance = (E) clz.newInstance();
            }else{
                Constructor<?> cons[] = clz.getConstructors();
                instance = (E) cons[0].newInstance(params);
            }
        }catch(Throwable t){
            t.printStackTrace();
            return null;
        }
        return instance;
    }

    /**
     * get class loader by path
     * @param path file path
     * @param parent paernet  class loader
     * @return class loader
     */
    public static URLClassLoader getClassLoaderByFile(String path, ClassLoader parent){
        URLClassLoader classloader = null;
        try {
            if(null == parent){
                parent = Thread.currentThread().getContextClassLoader();
            }
            classloader = URLClassLoader.newInstance(new URL[]{new File(path).toURI().toURL()},
                    parent);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return classloader;
    }
}
