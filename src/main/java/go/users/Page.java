package go.users;

import java.io.Serializable;
import java.util.ArrayList;

public class Page implements Serializable {
    private String pathname;
    private ArrayList<Integer> relatedCrumbsIds;


    public Page(String pathname){
        this.pathname = pathname;
        relatedCrumbsIds = new ArrayList<>();
    }

    public String getPathname() {
        return pathname;
    }

    public void addRelatedCrumbId(int relatedCrumbId) {
        relatedCrumbsIds.add(relatedCrumbId);
    }

    public ArrayList<Integer> getRelatedCrumbsIds() {
        return relatedCrumbsIds;
    }
}
