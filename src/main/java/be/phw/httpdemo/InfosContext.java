package be.phw.httpdemo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class InfosContext{

    private static final Logger logger = LoggerFactory.getLogger(InfosContext.class.getName());
    private static final ThreadLocal<String> calledUrl = new ThreadLocal<String>();
 
    private InfosContext() {
    }
 
    public static String getCalledUrl() {
        return (String)calledUrl.get();
    }
 
    public static void setCalledUrl(String url) {
        calledUrl.set(url);
    }

    public static void clearUrl() {
        calledUrl.remove();
    }
 
}
