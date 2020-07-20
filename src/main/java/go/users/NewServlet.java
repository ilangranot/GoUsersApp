package go.users;

import com.google.cloud.storage.*;

import java.net.URL;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Upload new crumb
 */
@SuppressWarnings("serial")
public class NewServlet extends HttpServlet {
    private static final String BUCKET_NAME = "gousers_crumbs";
    private static final String PROJECT_ID = "crumbs-storage-1591239911679";
    private static Storage storage = null;
    private Model model;
    private Gson gson;
    private final Logger log = LogManager.getLogger(this.getClass());

    @Override
    public void init() {
        model = new Model();
        gson = new Gson();
        storage = StorageOptions.newBuilder().setProjectId(PROJECT_ID).build().getService();
    }

    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        // pre-flight request processing
        resp.setHeader("Access-Control-Allow-Origin", req.getHeader("origin"));
        resp.setHeader("Access-Control-Allow-Methods", "OPTIONS, POST"); // HEAD, GET, PUT
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Headers, Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    @Override
    public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        try {
            JsonElement jsonElement = JsonParser.parseReader(req.getReader());
            Comm comm = gson.fromJson(jsonElement.getAsJsonObject().get("content"), Comm.class);
            comm.setStatus(Status.RESPONSE);
            if (comm.youtubeUrl.isEmpty())
                comm.uploadUrl = generateV4GPutObjectSignedUrl(comm, req.getHeader("origin"));
            else {
                URL ytURL = new URL(comm.youtubeUrl);
                String ytTag = ytURL.getQuery();
                comm.mediaLink = ytTag.substring(ytTag.length()-11);
            }
            model.addCrumb(comm);
            resp.getWriter().print(gson.toJson(comm, comm.getClass()));
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error(e.getMessage());
            Comm comm = new Comm();
            comm.setStatus(Status.ERROR);
            comm.setErrorMessage(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            resp.addHeader("Access-Control-Allow-Origin", req.getHeader("origin"));
            resp.addHeader("Access-Control-Allow-Methods", "POST");
            resp.addHeader("Access-Control-Allow-Credentials", "true");
            resp.addHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Headers, Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
        }
    }

    /**
     * Signing a URL requires Credentials which implement ServiceAccountSigner. These can be set
     * explicitly using the Storage.SignUrlOption.signWith(ServiceAccountSigner) option. If you don't,
     * you could also pass a service account signer to StorageOptions, i.e.
     * StorageOptions().newBuilder().setCredentials(ServiceAccountSignerCredentials). In this example,
     * neither of these options are used, which means the following code only works when the
     * credentials are defined via the environment variable GOOGLE_APPLICATION_CREDENTIALS, and those
     * credentials are authorized to sign a URL. See the documentation for Storage.signUrl for more
     * details.
     */
    public URL generateV4GPutObjectSignedUrl(Comm comm, String origin) throws StorageException {
        // Modify access list to allow all users with link to read file
        List<Acl> acls = new ArrayList<>();
        acls.add(Acl.of(Acl.User.ofAllUsers(), Acl.Role.READER));
        // Define Resource
        LocalDateTime localDateTime = LocalDateTime.now();
        String fileName = String.valueOf(origin.hashCode() + localDateTime.toString().hashCode()) + ".mp4";
        BlobInfo blobInfo = BlobInfo.newBuilder(BlobId.of(BUCKET_NAME, fileName)).setAcl(acls).build();
        comm.mediaLink = "https://storage.googleapis.com/gousers_crumbs/" + fileName;
        // Generate Signed URL
        Map<String, String> extensionHeaders = new HashMap<>();
        URL url = storage.signUrl(
                blobInfo,
                5,
                TimeUnit.MINUTES,
                Storage.SignUrlOption.httpMethod(HttpMethod.PUT),
                Storage.SignUrlOption.withExtHeaders(extensionHeaders),
                Storage.SignUrlOption.withV4Signature());
        return url;
    }
}