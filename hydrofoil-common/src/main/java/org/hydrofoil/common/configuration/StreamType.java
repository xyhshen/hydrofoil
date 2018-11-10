package org.hydrofoil.common.configuration;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.function.Function;

/**
 * stream type
 */
enum StreamType {

    /**
     * by local file,load stream
     */
    Local("local",(path)-> {
        try {
            return FileUtils.openInputStream(new File((String) path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }),
    /**
     * by resource
     */
    Resource("resource",(path)->{
        InputStream resourceAsStream = Thread.currentThread().
                getContextClassLoader().getResourceAsStream((String) path);
        if(resourceAsStream != null){
            return resourceAsStream;
        }
        return null;
    }),
    /**
     * stream
     */
    Stream("stream",(object)->(InputStream) object);

    private final String name;

    private final Function<Object,InputStream> process;

    StreamType(final String name,final Function<Object,InputStream> process){
        this.name = name;
        this.process = process;
    }

    /**
     * @return String
     * @see StreamType#name
     **/
    String getName() {
        return name;
    }

    InputStream getStream(Object value){
        return process.apply(value);
    }
}
