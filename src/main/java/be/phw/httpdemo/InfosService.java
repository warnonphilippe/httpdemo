package be.phw.httpdemo;


public class InfosService {


    public void log(String fileRequested) {
		System.out.println("Previous URL in ThreadLocal : " + InfosContext.getCalledUrl());
		System.out.println("Current URL requested : " + fileRequested);
		InfosContext.setCalledUrl(fileRequested);
		Integer cpt = InfosShared.getInstance().increment(fileRequested);
		System.out.println("# Appels : " + cpt);
	}

    
}
