package hotelappserver;

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.servlet.ServletException;
import javax.servlet.http.*;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Servlet handles /home requests
 * by:Gandhar Kulkarni
 */
public class HomeServlet extends HttpServlet {

    /**
     * Handles GET request to /home
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
            Template template = velocityEngine.getTemplate("templates/HomePage.html");
            String lastLoginDetails = DatabaseHelper.getInstance().getLastLoginDetails(username);
            StringWriter writer = new StringWriter();
            context.put("username", username);
            if(!lastLoginDetails.equals("N/A"))
                context.put("lastLogin", lastLoginDetails);
            else
                context.put("lastLogin", "");
            template.merge(context, writer);
            out.println(writer.toString());
            out.flush();
        }
    }
}
