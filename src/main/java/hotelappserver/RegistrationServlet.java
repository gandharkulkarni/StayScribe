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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Servlet handles /register requests
 * by:Gandhar Kulkarni
 */
public class RegistrationServlet extends HttpServlet {

    /**
     * Handles GET request to /register
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
        Template template = velocityEngine.getTemplate("templates/RegistrationForm.html");
        String usernameError = request.getParameter("username_error");
        usernameError = StringEscapeUtils.escapeHtml4(usernameError);
        if (usernameError != null && !usernameError.equals("")) {
            if (usernameError.equals("true")) {
                context.put("usernameError", true);
            }
        }
        String formatError = request.getParameter("format_error");
        if (formatError != null && !formatError.equals("")) {
            if (formatError.equals("true")) {
                context.put("formatError", true);
            }
        }
        StringWriter writer = new StringWriter();
        template.merge(context, writer);
        out.println(writer.toString());
    }

    /**
     * Handles POST request to /register
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
                if (checkUsername(username) && checkPassword(password)) {
                    DatabaseHelper databaseHelper = DatabaseHelper.getInstance();
                    if (!databaseHelper.checkUsernameAlreadyExists(username)) {
                        databaseHelper.registerUser(username, password);
                        response.sendRedirect("/login?register=true");
                    } else {
                        response.sendRedirect("/register?username_error=true");
                    }
                } else {
                    response.sendRedirect("/register?format_error=true");
                }
            }
        }

    }

    /**
     * Helper method to check the username against a pattern using regex
     *
     * @param username String
     * @return
     */
    private boolean checkUsername(String username) {
        String regex = "[A-Za-z][a-zA-Z0-9\\._]{5,15}";
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(username);
        if (matcher.find()) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Helper method to check the password against a pattern using regex
     *
     * @param password String
     * @return
     */
    private boolean checkPassword(String password) {
        String regex = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[._@$%#]).{8,}$";
        Pattern p = Pattern.compile(regex);
        Matcher matcher = p.matcher(password);
        if (matcher.find()) {
            return true;
        } else {
            return false;
        }
    }
}
