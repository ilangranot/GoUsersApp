package go.users;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * Servlet class deals with updating the model with a new CLICK posted to /click
 */
public class ClickServlet extends HttpServlet {
    private Model model;
    private final Logger log = LogManager.getLogger(this.getClass());

    @Override
    public void init() throws ServletException {
        log.info("Servlet " + getServletName() + " STARTED");
        model = new Model();
    }


    @Override
    protected void doOptions(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
    {
        // pre-flight request processing
        resp.setHeader("Access-Control-Allow-Origin", req.getHeader("origin"));
        resp.setHeader("Access-Control-Allow-Methods", "POST");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Headers, Origin, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
        resp.setStatus(HttpServletResponse.SC_OK);
    }


    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            model.updateClick(req);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e){
            log.error(e.getMessage());
            Comm comm = new Comm();
            comm.setStatus(Status.ERROR);
            comm.setErrorMessage(e.getMessage());
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        } finally {
            resp.addHeader("Access-Control-Allow-Origin", req.getHeader("origin"));
            resp.addHeader("Access-Control-Allow-Methods", "POST");
            resp.addHeader("Access-Control-Allow-Credentials", "true");
            resp.addHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Headers, Origin, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers"); //Accept, X-Requested-With,
        }
    }


    @Override
    public void destroy() {
        log.info("Servlet " + getServletName() + " STOPPED");
    }
}
