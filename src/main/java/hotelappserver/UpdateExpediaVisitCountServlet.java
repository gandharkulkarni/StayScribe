package hotelappserver;

import hotelapp.Hotel;
import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet handles requests on /updateVisitCount path
 * By: Gandhar Kulkarni
 */
public class UpdateExpediaVisitCountServlet extends HttpServlet {

    /**
     * Handles GET request on /updateVisitCount path
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
            response.setStatus(HttpServletResponse.SC_OK);
            String hotelId = request.getParameter("hotelId");
            String hotelLink = request.getParameter("link");

            if (hotelId != null && !hotelId.isEmpty())
                hotelId = StringEscapeUtils.escapeHtml4(hotelId);
            if (hotelLink != null && !hotelLink.isEmpty())
                hotelLink = StringEscapeUtils.escapeHtml4(hotelLink);

            String username = session.getAttribute("username").toString();
            Hotel hotel = DatabaseHelper.getInstance().getHotelByHotelId(hotelId);
            int visitCount = DatabaseHelper.getInstance().getExpediaPageVisitCount(username, hotelId);
            if (visitCount == 0) {
                DatabaseHelper.getInstance().insertExpediaPageVisitCount(username, hotelId, hotel.getHotelName(), hotelLink);
            } else {
                DatabaseHelper.getInstance().updateExpediaPageVisitCount(username, hotelId);
            }
            PrintWriter out = response.getWriter();
            out.println(true);
            out.flush();
        }
    }
}

