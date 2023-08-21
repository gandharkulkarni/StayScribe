package hotelappserver;

import org.apache.commons.text.StringEscapeUtils;
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
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Servlet handles /login requests
 * by: Gandhar Kulkarni
 */
public class LoginServlet extends HttpServlet {

    /**
     * Handles GET request to /login
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("username") != null) {
            response.sendRedirect("/home");
        }
        PrintWriter out = response.getWriter();
        response.setContentType("text/html");
        response.setStatus(HttpServletResponse.SC_OK);

        VelocityEngine velocityEngine = (VelocityEngine) request.getServletContext().getAttribute("velocityTemplateEngine");
        VelocityContext context = new VelocityContext();
        Template template = velocityEngine.getTemplate("templates/LoginForm.html");
        String register = request.getParameter("register");
        register = StringEscapeUtils.escapeHtml4(register);
        if (register != null && !register.equals("")) {
            if (register.equals("true")) {
                context.put("register", true);
            }
        }
        String credentialError = request.getParameter("credential_error");
        credentialError = StringEscapeUtils.escapeHtml4(credentialError);
        if (credentialError != null && !credentialError.equals("")) {
            if (credentialError.equals("true")) {
                context.put("credentialError", true);
            }
        }
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.println(writer.toString());

    }

    /**
     * Handles POST request to /login
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String username = request.getParameter("username");
        String password = request.getParameter("password");
        if (username != null && !username.equals("")) {
            if (password != null && !password.equals("")) {
                username = StringEscapeUtils.escapeHtml4(username);
                password = StringEscapeUtils.escapeHtml4(password);
                DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
                boolean validUser = databaseHelper.authenticateUser(username, password);
                if (validUser) {
                    HttpSession session = request.getSession();
                    session.setAttribute("username", username);
                    session.setAttribute("currentLogin", LocalDateTime.now().format(DateTimeFormatter.ofPattern("MM-dd-yyyy hh:mm:ss a")).toString());
                    response.sendRedirect("/home");
                } else {
                    response.sendRedirect("/login?credential_error=true");
                }

            }
        }
    }
}
