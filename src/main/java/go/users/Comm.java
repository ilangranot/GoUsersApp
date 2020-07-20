package go.users;

import java.net.URL;

public class Comm {
    private Status status;
    private String errorMessage;
    public String ns; // namespace
    public String pathname; // window.location.pathname from script.js post to /page
    public String hostname;
    public String subject;
    public String note;
    public String fileName;
    public String youtubeUrl;
    public String mediaLink;
    public String eventTarget;
    public String eventText;
    public URL uploadUrl;
    public Crumb[] crumbs;

    public void setStatus(Status status) {
        this.status = status;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Status getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }
}
