package go.users;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.*;

/**
 * Servlet class
 *  1. Maybe OPTIONS is not needed?
 */

public class PageServlet extends HttpServlet {
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
        resp.setHeader("Access-Control-Allow-Methods", "OPTIONS, POST");
        resp.setHeader("Access-Control-Allow-Credentials", "true");
        resp.setHeader("Access-Control-Allow-Headers", "Access-Control-Allow-Headers, Origin, Accept, X-Requested-With, Content-Type, Access-Control-Request-Method, Access-Control-Request-Headers");
        resp.setStatus(HttpServletResponse.SC_OK);
    }

    // POST to /page - new/reload site/page/AJAX
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            model.updatePage(req);
            resp.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e){
            log.error(e.getMessage());
            log.error(e.getStackTrace());
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

    @Override
    public void destroy() {
        log.info("Servlet " + getServletName() + " STOPPED");
    }
}
