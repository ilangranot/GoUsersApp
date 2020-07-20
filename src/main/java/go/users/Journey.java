package go.users;

import java.io.Serializable;

/**
 * Represents a walk during client use
 */
public class Journey implements Serializable {
    private Client client;
    private Site site;

    public Journey(Site site) {
        this.site = site;
    }

    public Site getSite() {
        return site;
    }
}
