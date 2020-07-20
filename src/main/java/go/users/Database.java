package go.users;

import com.google.appengine.api.NamespaceManager;
import com.google.cloud.datastore.*;
import com.google.gson.Gson;
import java.util.Map;

//System.out.printf("Saved %s: %s%n", entity.getKey().getName(), entity.getString("value"));

public class Database {
    private final static String DEFAULT_NAMESPACE = "gu_global";

    public void storeGlobal(String namespace, String kind, String name, String key, String value) throws Exception {
        if (namespace == null)
            namespace = DEFAULT_NAMESPACE;
        NamespaceManager.set(namespace);
        Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
        Key entryKey = datastore.newKeyFactory().setKind(kind).newKey(name);
        Entity entity = Entity.newBuilder(entryKey)
                .set(key, value)
                .build();
        datastore.put(entity);
    }


    public void storeSite(String namespace, Site site) throws Exception {
        if (namespace == null || namespace.isEmpty())
            namespace = DEFAULT_NAMESPACE;
        NamespaceManager.set(namespace);
        Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
        String kind = "sites";
        String name = site.getHostname();
        Key sitekey = datastore.newKeyFactory().setKind(kind).newKey(name);
        Gson gson = new Gson();
        Entity entity = Entity.newBuilder(sitekey)
                .set("value", gson.toJson(site))
                .build();
        datastore.put(entity);
    }

    public void storeCrumb(String namespace, int id, Crumb crumb) throws Exception {
        if (namespace == null || namespace.isEmpty())
            namespace = DEFAULT_NAMESPACE;
        NamespaceManager.set(namespace);
        Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
        String kind = "crumbs";
        String name = String.valueOf(id);
        Key sitekey = datastore.newKeyFactory().setKind(kind).newKey(name);
        Gson gson = new Gson();
        Entity entity = Entity.newBuilder(sitekey)
                .set("value", gson.toJson(crumb))
                .build();
        datastore.put(entity);
    }

    public String retrieveGlobal(String namespace, String kind, String name, String key){
        String string = null;
        if (namespace == null)
            namespace = DEFAULT_NAMESPACE;
        NamespaceManager.set(namespace);
        Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
        Key entryKey = datastore.newKeyFactory().setKind(kind).newKey(name);
        Entity entity = datastore.get(entryKey);
        if (entity != null) {
            Map<String, Value<?>> map = entity.getProperties();
            string = (String) map.get(key).get();
        }
        return string;
    }

    public Site retrieveSite(String namespace, String hostname){
        if (namespace == null)
            namespace = DEFAULT_NAMESPACE;
        Site site = null;
        NamespaceManager.set(namespace);
        Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
        String kind = "sites";
        String name = hostname;
        Key key = datastore.newKeyFactory().setKind(kind).newKey(name);
        Entity entity = datastore.get(key);
        if (entity != null) {
            Map<String, Value<?>> map = entity.getProperties();
            Gson gson = new Gson();
            site = gson.fromJson((String) map.get("value").get(), Site.class);
        }
        return site;
    }

    public Crumb retrieveCrumb(String namespace, int id){
        if (namespace == null)
            namespace = DEFAULT_NAMESPACE;
        Crumb crumb = null;
        NamespaceManager.set(namespace);
        Datastore datastore = DatastoreOptions.getDefaultInstance().getService();
        String kind = "crumbs";
        String name = String.valueOf(id);
        Key key = datastore.newKeyFactory().setKind(kind).newKey(name);
        Entity entity = datastore.get(key);
        if (entity != null) {
            Map<String, Value<?>> map = entity.getProperties();
            Gson gson = new Gson();
            crumb = gson.fromJson((String) map.get("value").get(), Crumb.class);
        }
        return crumb;
    }
}
