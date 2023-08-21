package hotelappserver;

import com.google.gson.JsonObject;
import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Handles request on /fetchReviews path
 * By: Gandhar Kulkarni
 */
public class ReviewFetcherServlet extends HttpServlet {

    /**
     * Handles GET Request on /fetchReviews path
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
            String offset = request.getParameter("offset");
            String limit = request.getParameter("limit");
            if (hotelId != null && !hotelId.isEmpty())
                hotelId = StringEscapeUtils.escapeHtml4(hotelId);

            if (offset != null && !offset.isEmpty())
                offset = StringEscapeUtils.escapeHtml4(offset);

            if (limit != null && !limit.isEmpty())
                limit = StringEscapeUtils.escapeHtml4(limit);

            JsonResponseHelper jsonResponseHelper = new JsonResponseHelper();
            JsonObject reviewJson = jsonResponseHelper.getAllReviewsForHotelInJsonFormat(hotelId, limit, offset);
            PrintWriter out = response.getWriter();
            out.println(reviewJson);
            out.flush();
        }
    }
}
