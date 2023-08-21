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
import javax.xml.crypto.Data;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;

/**
 * Servlet handles /addReview requests
 * by:Gandhar Kulkarni
 */
public class AddReviewServlet extends HttpServlet {
    /**
     * Handles GET request to /addReview
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
            Template template = velocityEngine.getTemplate("templates/AddReview.html");
            ThreadSafeHotelDataBuilder threadSafeHotelDataBuilder = (ThreadSafeHotelDataBuilder) request.getServletContext().getAttribute("threadSafeHotelData");
            String hotelId = request.getParameter("hotelId");
            if (hotelId != null && !hotelId.equals("")) {
                hotelId = StringEscapeUtils.escapeHtml4(hotelId);
            }
            Hotel hotel = DatabaseHelper.getInstance().getHotelByHotelId(hotelId);
            context.put("hotel", hotel);
            StringWriter writer = new StringWriter();
            template.merge(context, writer);
            out.println(writer.toString());
            out.flush();
        }
    }

    /**
     * Handles POST request to /addReview
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
            ThreadSafeHotelDataBuilder threadSafeHotelDataBuilder = (ThreadSafeHotelDataBuilder) request.getServletContext().getAttribute("threadSafeHotelData");
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
            String hotelId = request.getParameter("hotelId");
            String reviewTitle = request.getParameter("reviewTitle");
            String reviewText = request.getParameter("reviewText");
            String overAllRating = request.getParameter("overAllRating");

            if (hotelId != null && !hotelId.equals("")) {
                hotelId = StringEscapeUtils.escapeHtml4(hotelId);
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
            String reviewId = generateReviewId(hotelId);
            Set<String> reviewIdSet = DatabaseHelper.getInstance().getAllReviewId();
            while (reviewIdSet.contains(reviewId)) {
                reviewId = generateReviewId(hotelId);
            }
            Review newReview = new Review(hotelId, reviewId, Double.parseDouble(overAllRating), reviewTitle, reviewText, username, submissionDate);
            ArrayList<Review> reviews = new ArrayList<>();
            reviews.add(newReview);
            DatabaseHelper.getInstance().insertRecordIntoReviewsTable(reviews);
            response.sendRedirect("/hotelInfo?hotelId=" + hotelId);
        }

    }

    private String generateReviewId(String hotelId) {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("review");
        stringBuilder.append(hotelId);
        Random rand = new Random();
        int randomInt = rand.nextInt(99999);
        stringBuilder.append(randomInt);
        return stringBuilder.toString();
    }
}
