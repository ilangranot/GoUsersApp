package go.users;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Site implements Serializable, DatabaseStorable {
    private String hostname;
    private Map<String,Page> pagesMap;
    private double averageIdleTimeout;
    private static final int AVERAGE_TIMEOUT_NOT_SET = -1; // REMOVE


    public Site(String hostname, String pathname){
        this.averageIdleTimeout = AVERAGE_TIMEOUT_NOT_SET;
        this.hostname = hostname;
        if (pagesMap != null){
            if (!pagesMap.containsKey(pathname))
                pagesMap.put(pathname, new Page(pathname));
        } else {
            pagesMap = new HashMap<>();
            pagesMap.put(pathname, new Page(pathname));
        }
    }

    public void updateSite(String pathname){
        if (!pagesMap.containsKey(pathname))
            pagesMap.put(pathname, new Page(pathname));
    }

    public String getId() {
        return hostname;
    }

    public String getHostname() {
        return hostname;
    }

    public void setAverageIdleTimeout(double averageIdleTimeout) {
        this.averageIdleTimeout = averageIdleTimeout;
    }

    public double getAverageIdleTimeout() {
        return averageIdleTimeout;
    }

    public void addRelatedCrumbId(String pathname, int relatedCrumbId) {
        pagesMap.get(pathname).addRelatedCrumbId(relatedCrumbId);
    }

    public Map<String, Page> getPagesMap() {
        return pagesMap;
    }

    public ArrayList<Integer> generateIdsList(String pathname) {
        ArrayList<Integer> results = null;
        if (pagesMap.get(pathname).getRelatedCrumbsIds() != null)
            results = new ArrayList<>(pagesMap.get(pathname).getRelatedCrumbsIds());
        else
            results = new ArrayList<>();
        for (String path : pagesMap.keySet()){
            ArrayList<Integer> crumbs = pagesMap.get(path).getRelatedCrumbsIds();
            for (int crumb : crumbs){
                if (!results.contains(crumb))
                    results.add(crumb);
            }
        }
        return results;
    }
}
