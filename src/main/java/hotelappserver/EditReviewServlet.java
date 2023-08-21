package hotelappserver;

import hotelapp.Hotel;
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
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * Servlet handles /editReview requests
 * by:Gandhar Kulkarni
 */
public class EditReviewServlet extends HttpServlet {

    /**
     * Handles GET request to /editReview
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
            String hotelId = request.getParameter("hotelId");
            String reviewId = request.getParameter("reviewId");
            if (hotelId != null && !hotelId.equals("")) {
                hotelId = StringEscapeUtils.escapeHtml4(hotelId);
            }
            if (reviewId != null && !reviewId.equals("")) {
                reviewId = StringEscapeUtils.escapeHtml4(reviewId);
            }
            Hotel hotel = DatabaseHelper.getInstance().getHotelByHotelId(hotelId);
            Set<Review> reviewSet = DatabaseHelper.getInstance().getReviewByHotelId(hotelId);

            for (Review review : reviewSet) {
                if (review.getReviewId().equals(reviewId)) {
                    context.put("review", review);
                    break;
                }
            }
            context.put("hotel", hotel);
            StringWriter writer = new StringWriter();
            template.merge(context, writer);
            out.println(writer.toString());
            out.flush();
        }

    }

    /**
     * Handles POST request to /editReview
     *
     * @param request
     * @param response
     * @throws ServletException
     * @throws IOException
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        if (session.getAttribute("username") == null) {
            response.sendRedirect("/login");
        } else {
            PrintWriter out = response.getWriter();
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);

            String hotelId = request.getParameter("hotelId");
            String reviewId = request.getParameter("reviewId");
            String reviewTitle = request.getParameter("reviewTitle");
            String reviewText = request.getParameter("reviewText");
            String overAllRating = request.getParameter("overAllRating");
            if (hotelId != null && !hotelId.equals("")) {
                hotelId = StringEscapeUtils.escapeHtml4(hotelId);
            }
            if (reviewId != null && !reviewId.equals("")) {
                reviewId = StringEscapeUtils.escapeHtml4(reviewId);
            }
            if (reviewTitle != null && !reviewTitle.equals("")) {
                reviewTitle = StringEscapeUtils.escapeHtml4(reviewTitle);
            }
            if (reviewText != null && !reviewText.equals("")) {
                reviewText = StringEscapeUtils.escapeHtml4(reviewText);
            }
            if (overAllRating != null && !overAllRating.equals("")) {
                overAllRating = StringEscapeUtils.escapeHtml4(overAllRating);
            }
            String submissionDate = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'").format(LocalDateTime.now());
            String username = session.getAttribute("username").toString();

            Review updatedReview = new Review(hotelId, reviewId, Double.parseDouble(overAllRating), reviewTitle, reviewText, username, submissionDate);
            DatabaseHelper.getInstance().updateReviewDetails(updatedReview);
            response.sendRedirect("/hotelInfo?hotelId=" + hotelId);
        }
    }
}
