package be.phw.httpdemo;

import java.util.HashMap;
import java.util.Map;

public class InfosShared {

    private static InfosShared instance;

    public static InfosShared getInstance() {
        if (instance == null) {
            instance = new InfosShared();
        }
        return instance;
    }

    private Map<String, Integer> countMap;

    private InfosShared(){
        countMap = new HashMap<>();
    }

    public synchronized Integer getCount(String url){
        Integer cpt = (Integer) countMap.get(url);
        return (cpt != null) ? cpt : 0;
    }

    public synchronized Integer increment(String url){
        Integer count = getCount(url);
        countMap.put(url, count + 1);
        return count;
    }
    
}
