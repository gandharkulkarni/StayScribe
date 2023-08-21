package hotelappserver;

import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet to handle request to /favouriteHotel path
 */
public class AddFavouriteHotelServlet extends HttpServlet {

    /**
     * Handles GET request to /favouriteHotel path
     *
     * @param request  String
     * @param response String
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("username") == null) {
            response.sendRedirect("/login");
        } else {
            String username = session.getAttribute("username").toString();
            String hotelId = request.getParameter("hotelId");
            if (hotelId != null && !hotelId.isEmpty())
                hotelId = StringEscapeUtils.escapeHtml4(hotelId);
            PrintWriter out = response.getWriter();
            if (!DatabaseHelper.getInstance().checkFavouriteHotelDetailsExists(username, hotelId)) {
                DatabaseHelper.getInstance().insertFavouriteHotelDetails(username, hotelId);
                out.println(true);
            }
            else
                out.println(false);
            out.flush();
        }
    }
}
