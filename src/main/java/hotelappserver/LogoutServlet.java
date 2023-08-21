package hotelappserver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.crypto.Data;
import java.io.IOException;

/**
 * Servlet handles /logout requests
 * by: Gandhar Kulkarni
 */
public class LogoutServlet extends HttpServlet {

    /**
     * Handles GET request to /logout
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
            String username = session.getAttribute("username").toString();
            String currentLogin = session.getAttribute("currentLogin").toString();
            if (DatabaseHelper.getInstance().getLastLoginDetails(username).equals("N/A")) {
                DatabaseHelper.getInstance().insertLastLoginDetails(username, currentLogin);
            } else {
                DatabaseHelper.getInstance().updateLastLoginDetails(username, currentLogin);
            }
            session.setAttribute("username", null);
            session.invalidate();
            response.sendRedirect("/login");
        } else {
            response.sendRedirect("/login");
        }
    }
}
