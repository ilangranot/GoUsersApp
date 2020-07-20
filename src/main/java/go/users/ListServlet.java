package go.users;

import com.google.gson.Gson;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.*;
import java.io.*;

/**
 * GetServlet class
 * REMOVE OR UNCOMMENT COMMENTS //
 */

public class ListServlet extends HttpServlet {
    private Model model;
    private Gson gson;
    private final Logger log = LogManager.getLogger(this.getClass());

    @Override
    public void init() throws ServletException {
        log.info("Servlet " + getServletName() + " STARTED");
        //log.info(System.getenv("GOOGLE_APPLICATION_CREDENTIALS"));
        gson = new Gson();
        model = new Model();
    }


    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        // pre-flight request processing
        resp.setHeader("Access-Control-Allow-Origin", req.getHeader("origin"));
        resp.setHeader("Access-Control-Allow-Methods", "OPTIONS, POST"); // HEAD, GET, PUT
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Headers, Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
        //resp.setHeader("Access-Control-Max-Age", "86400");
        resp.setStatus(HttpServletResponse.SC_OK);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Comm comm = new Comm();
        try {
            comm.crumbs = model.getCrumbsArray(req);
            comm.setStatus(Status.RESPONSE);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e){
            log.error(e.getMessage());
            log.error(e.getStackTrace());
            comm.setStatus(Status.ERROR);
            comm.setErrorMessage(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            String jsonString = gson.toJson(comm);
            resp.setContentType("application/json");
            resp.addHeader("Access-Control-Allow-Origin", req.getHeader("origin"));
            resp.addHeader("Access-Control-Allow-Methods", "POST");
            resp.addHeader("Access-Control-Allow-Credentials", "true");
            resp.addHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Headers, Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
            resp.getWriter().print(jsonString);
        }
    }

    @Override
    public void destroy() {
        log.info("Servlet " + getServletName() + " STOPPED");
    }
}
