package hotelappserver;

import hotelapp.Review;
import hotelapp.ThreadSafeHotelDataBuilder;
import org.apache.commons.text.StringEscapeUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.VelocityEngine;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Set;

/**
 * Servlet handles /deleteReview requests
 * by:Gandhar Kulkarni
 */
public class DeleteReviewServlet extends HttpServlet {

    /**
     * Handles GET request to /deleteReview
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
            VelocityEngine velocityEngine = (VelocityEngine) request.getServletContext().getAttribute("velocityTemplateEngine");
            VelocityContext context = new VelocityContext();
            Template template = velocityEngine.getTemplate("templates/EditReview.html");
            ThreadSafeHotelDataBuilder threadSafeHotelDataBuilder = (ThreadSafeHotelDataBuilder) request.getServletContext().getAttribute("threadSafeHotelData");
            String hotelId = request.getParameter("hotelId");
            String reviewId = request.getParameter("reviewId");
            if (hotelId != null && !hotelId.equals("")) {
                hotelId = StringEscapeUtils.escapeHtml4(hotelId);
            }
            if (reviewId != null && !reviewId.equals("")) {
                reviewId = StringEscapeUtils.escapeHtml4(reviewId);
            }
            Set<Review> reviewSet = DatabaseHelper.getInstance().getReviewByHotelId(hotelId);
            Review deleteReview = null;
            for (Review review : reviewSet) {
                if (review.getReviewId().equals(reviewId)) {
                    deleteReview = review;
                }
            }
            if (deleteReview != null) {
                DatabaseHelper.getInstance().deleteReview(deleteReview);
            }
            response.sendRedirect("/hotelInfo?hotelId=" + hotelId);
        }
    }
}
