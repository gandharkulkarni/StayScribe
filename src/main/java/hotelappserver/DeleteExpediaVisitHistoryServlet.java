package hotelappserver;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;

/**
 * Servlet handles request on /clearVisitHistory path
 */
public class DeleteExpediaVisitHistoryServlet extends HttpServlet {
    /**
     * Handles GET request on /clearVisitHistory path
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("username") == null) {
            response.sendRedirect("/login");
        } else {
            PrintWriter out = response.getWriter();
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
            String username = session.getAttribute("username").toString();

            VelocityEngine velocityEngine = (VelocityEngine) request.getServletContext().getAttribute("velocityTemplateEngine");
            VelocityContext context = new VelocityContext();
            Template template = velocityEngine.getTemplate("templates/ExpediaVisitHistory.html");
            DatabaseHelper.getInstance().clearExpediaVisitHistory(username);
            List<List<String>> visitHistory = DatabaseHelper.getInstance().getExpediaVisitHistory(username);
            StringWriter writer = new StringWriter();
            context.put("username", username);
            context.put("visitHistory", visitHistory);
            template.merge(context, writer);
            out.println(writer.toString());
            out.flush();
        }
    }
}
