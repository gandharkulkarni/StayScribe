package hotelappserver;

import hotelapp.Hotel;
import hotelapp.Review;
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
import java.util.Set;

/**
 * Servlet handles /searchReview requests
 * by:Gandhar Kulkarni
 */
public class HotelInfoServlet extends HttpServlet {

    /**
     * Handles GET request to /searchReview
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

            String hotelId = request.getParameter("hotelId");
            if (hotelId != null && !hotelId.equals("")) {
                hotelId = StringEscapeUtils.escapeHtml4(hotelId);
            }
            Hotel hotel = DatabaseHelper.getInstance().getHotelByHotelId(hotelId);

            VelocityEngine velocityEngine = (VelocityEngine) request.getServletContext().getAttribute("velocityTemplateEngine");
            VelocityContext context = new VelocityContext();
            String username = session.getAttribute("username").toString();
            Template template = velocityEngine.getTemplate("templates/HotelReviews.html");
            String expediaLink = getExpediaLinkForHotel(hotel);
            double averageRating = DatabaseHelper.getInstance().getAverageRatingForHotel(hotelId);
            context.put("hotel", hotel);
            context.put("expediaLink", expediaLink);
            context.put("username", username);
            context.put("avgOverallRating", averageRating);
            StringWriter writer = new StringWriter();
            template.merge(context, writer);
            out.println(writer.toString());
        }
    }

    /**
     * Helper method to generate expedia link for hotel
     *
     * @param hotel
     * @return
     */
    private String getExpediaLinkForHotel(Hotel hotel) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("https://www.expedia.com/");
        stringBuilder.append(hotel.getHotelCity());
        stringBuilder.append("-");
        stringBuilder.append(hotel.getHotelName().replaceAll(" ", "-"));
        stringBuilder.append(".");
        stringBuilder.append("h");
        stringBuilder.append(hotel.getHotelId());
        stringBuilder.append(".");
        stringBuilder.append("Hotel-Information");
        return stringBuilder.toString();
    }
}
