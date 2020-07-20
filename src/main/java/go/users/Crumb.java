package go.users;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Represents a Crumb
 */
public class Crumb implements Serializable {
    private String subject;
    private List<String> notes;
    private String url;
    private String fileName;

    public Crumb(String subject){
        this.subject = subject;
        notes = new ArrayList<>();
    }
    public String getSubject()
    {
        return subject;
    }

    public void setSubject(String subject)
    {
        this.subject = subject;
    }

    public void addNote(String note){
        notes.add(note);
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public String toString() {
        return "Subject:" + subject +
                ", Notes:" + (notes.size() > 0 ? notes.get(0) : "empty") +
                ", Url: " + (url != null ? url : "empty") ;
    }
}
