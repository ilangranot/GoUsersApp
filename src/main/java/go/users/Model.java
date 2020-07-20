package go.users;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Random;

/**
 * Represents the Model of the Websites and Crumbs knowledge-base
 */
public class Model implements Serializable {
    private final Logger log;
    private Gson gson;
    private Database database;

    /**
     * Constructor
     */
    public Model(){
        gson = new Gson();
        log = LogManager.getLogger(this.getClass());
        database = new Database();
    }

    /**
     * Loads / Refresh SITE
     * @param request the user's request
     */
    public void updatePage(HttpServletRequest request) throws IOException {
        JsonElement jsonElement = JsonParser.parseReader(request.getReader());
        Comm comm = gson.fromJson(jsonElement.getAsJsonObject().get("content"), Comm.class);
        Site site;
        try {
            if (database.retrieveSite(comm.ns, comm.hostname) != null) {
                site = database.retrieveSite(comm.ns, comm.hostname);
                site.updateSite(comm.pathname);
                database.storeSite(comm.ns, site);
            } else {
                site = new Site(comm.hostname, comm.pathname);
                database.storeSite(comm.ns, site);
            }
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * Updates a new interaction into current PageScreen
     * @param req the user's request
     */
    public void updateClick(HttpServletRequest req) throws IOException {
        try {
            JsonElement jsonElement = JsonParser.parseReader(req.getReader());
            Comm comm = gson.fromJson(jsonElement.getAsJsonObject().get("content"), Comm.class);
            Site site = database.retrieveSite(comm.ns, comm.hostname);
            //site.updateInteraction(comm); // UPDATE INTERACTION, DOESN'T DO ANYTHING YET - DOESN'T DO ANYTHING YET - DOESN'T DO ANYTHING YET
        } catch (Exception e) {
            log.error(e.getMessage());
        }
    }

    /**
     * Gets crumb
     * @param id serial number of crumb
     * @return the crumb object
     */
    public Crumb getCrumbFromDb(String namespace, int id){
        return database.retrieveCrumb(namespace, id);
    }

    /**
     * Adds a crumb to a SITE according to PAGE
     * @param comm the communication object
     */
    public void addCrumb(Comm comm) {
        try {
            Crumb crumb = new Crumb(comm.subject);
            if (comm.note != null)
                crumb.addNote(comm.note);
            crumb.setUrl(comm.mediaLink);
            crumb.setFileName(comm.fileName);
            Site site = database.retrieveSite(comm.ns, comm.hostname);
            synchronized (this) {
                int crumbId;
                if (database.retrieveGlobal(comm.ns, "globals", "crumbs", "lastIndex") != null)
                    crumbId = Integer.valueOf(database.retrieveGlobal(comm.ns, "globals", "crumbs", "lastIndex"));
                else
                    crumbId = 1;
                database.storeCrumb(comm.ns, crumbId, crumb);
                site.addRelatedCrumbId(comm.pathname, crumbId);
                crumbId++;
                database.storeSite(comm.ns, site);
                database.storeGlobal(comm.ns, "globals", "crumbs", "lastIndex", String.valueOf(crumbId));
            }
        } catch (Exception e){
            log.error(e.getMessage());
        }
    }


    /**
     * Gets an array of related crumbs
     * @param req the user's reuqest
     * @return a list of crumbs
     */
    public Crumb[] getCrumbsArray(HttpServletRequest req) {
        Crumb[] crumbs = null;
        try {
            JsonElement jsonElement = JsonParser.parseReader(req.getReader());
            Comm comm = gson.fromJson(jsonElement.getAsJsonObject().get("content"), Comm.class);
            Site site = database.retrieveSite(comm.ns, comm.hostname);
            ArrayList<Integer> relatedCrumbs = site.generateIdsList(comm.pathname);
            crumbs = new Crumb[relatedCrumbs.size()];
            for (int i = 0; i < relatedCrumbs.size(); i++)
                crumbs[i] = getCrumbFromDb(comm.ns, relatedCrumbs.get(i));
        } catch (Exception e) {
            log.error(e.getMessage());
        } finally {
            return crumbs;
        }
    }


    public String generatePassphrase(int length){
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ"
                + "abcdefghijklmnopqrstuvwxyz"
                + "0123456789"
                + "!@#$%^&*_=+-.()";
        Random rndm_method = new Random();
        char[] password = new char[length];
        for (int i = 0; i < length; i++)
        {
            password[i] = chars.charAt(rndm_method.nextInt(chars.length()));
        }
        return new String(password);
    }

}
