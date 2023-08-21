package hotelappserver;

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
import java.util.List;

/**
 * Servlet handles request on /favourites path
 * By: Gandhar Kulkarni
 */
public class ViewFavouriteHotelServlet extends HttpServlet {
    /**
     * Handles Get request on /favourites path
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
            String username = session.getAttribute("username").toString();
            List<List<String>> favouriteHotels = DatabaseHelper.getInstance().getFavouriteHotels(username);
            VelocityEngine velocityEngine = (VelocityEngine) request.getServletContext().getAttribute("velocityTemplateEngine");
            VelocityContext context = new VelocityContext();
            Template template = velocityEngine.getTemplate("templates/FavouriteHotels.html");
            context.put("hotels", favouriteHotels);
            StringWriter writer = new StringWriter();
            template.merge(context, writer);
            out.println(writer.toString());


        }

    }
}
