package hotelappserver;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

/**
 * Servlet to handle requests to /clearFavourites path
 */
public class ClearFavouriteHotelServlet extends HttpServlet {

    /**
     * Handles GET request to /clearFavourites path
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
            String username = session.getAttribute("username").toString();
            DatabaseHelper.getInstance().clearFavouriteHotels(username);
            response.sendRedirect("/favourites");
        }
    }
}
