package hotelappserver;

import hotelapp.Hotel;
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
import java.util.*;

/**
 * Servlet handles /searchHotels requests
 * by:Gandhar Kulkarni
 */
public class HotelSearchServlet extends HttpServlet {

    /**
     * Handles GET request to /searchHotels
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
            Template template = velocityEngine.getTemplate("templates/HotelInfo.html");

            StringWriter writer = new StringWriter();
            template.merge(context, writer);
            out.println(writer.toString());
        }
    }

    /**
     * Handles POST request to /searchHotels
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
            DatabaseHelper dbHelper = DatabaseHelper.getInstance();

            SortedSet<Hotel> hotelSet = dbHelper.getAllHotels();
            String searchedKeyWord = request.getParameter("search");
            SortedSet<Hotel> hotels = new TreeSet<>(new Comparator<Hotel>() {
                @Override
                public int compare(Hotel hotelOne, Hotel hotelTwo) {
                    return hotelOne.getHotelId().compareTo(hotelTwo.getHotelId());
                }
            }
            );
            if (searchedKeyWord != null && !searchedKeyWord.equals("")) {
                searchedKeyWord = StringEscapeUtils.escapeHtml4(searchedKeyWord);
                searchedKeyWord = searchedKeyWord.toLowerCase();
            } else {
                searchedKeyWord = null;
            }
            for (Hotel hotel : hotelSet) {
                if (searchedKeyWord != null) {
                    String hotelName = hotel.getHotelName().toLowerCase();
                    hotelName = hotelName.replaceAll("[^A-Za-z ]", " ");
                    if (hotelName.contains(searchedKeyWord)) {
                        hotels.add(hotel);
                    }
                } else {
                    hotels.add(hotel);
                }
            }
            PrintWriter out = response.getWriter();
            response.setContentType("text/html");
            response.setStatus(HttpServletResponse.SC_OK);
            VelocityEngine velocityEngine = (VelocityEngine) request.getServletContext().getAttribute("velocityTemplateEngine");
            VelocityContext context = new VelocityContext();
            Template template = velocityEngine.getTemplate("templates/HotelInfo.html");
            context.put("hotels", hotels);
            StringWriter writer = new StringWriter();
            template.merge(context, writer);
            out.println(writer.toString());
        }
    }
}
