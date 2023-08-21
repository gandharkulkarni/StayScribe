package hotelappserver;

import hotelapp.LogHelper;
import hotelapp.ThreadSafeHotelDataBuilder;
import org.apache.velocity.app.VelocityEngine;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;

/**
 * Server class
 * By: Gandhar Kulkarni
 */
public class HotelServer {

    private static final int PORT = 8080;
    private ThreadSafeHotelDataBuilder threadSafeHotelDataBuilder;

    /**
     * Constructor to initialize threadsafe object.
     *
     * @param threadSafeHotelDataBuilder
     */
    public HotelServer(ThreadSafeHotelDataBuilder threadSafeHotelDataBuilder) {
        this.threadSafeHotelDataBuilder = threadSafeHotelDataBuilder;
    }

    public static void main(String[] args) throws Exception {
    }

    /**
     * Starts the server by listening on port
     */
    public void startServer() {
        Server server = new Server(PORT);
        ServletContextHandler handler = new ServletContextHandler(ServletContextHandler.SESSIONS);

        handler.addServlet(RegistrationServlet.class, "/register");
        handler.addServlet(LoginServlet.class, "/login");
        handler.addServlet(LoginServlet.class, "/");
        handler.addServlet(HomeServlet.class, "/home");
        handler.addServlet(LogoutServlet.class, "/logout");
        handler.addServlet(HotelSearchServlet.class, "/searchHotels");
        handler.addServlet(HotelInfoServlet.class, "/hotelInfo");
        handler.addServlet(AddReviewServlet.class, "/addReview");
        handler.addServlet(EditReviewServlet.class, "/editReview");
        handler.addServlet(DeleteReviewServlet.class, "/deleteReview");
        handler.addServlet(ReviewFetcherServlet.class, "/fetchReviews");
        handler.addServlet(UpdateExpediaVisitCountServlet.class, "/updateVisitCount");
        handler.addServlet(ViewExpediaVisitServlet.class, "/viewExpediaVisitHistory");
        handler.addServlet(DeleteExpediaVisitHistoryServlet.class, "/clearVisitHistory");
        handler.addServlet(AddFavouriteHotelServlet.class, "/favouriteHotel");
        handler.addServlet(ViewFavouriteHotelServlet.class, "/favourites");
        handler.addServlet(ClearFavouriteHotelServlet.class, "/clearFavourites");

        // initialize Velocity
        VelocityEngine velocityEngine = new VelocityEngine();
        velocityEngine.init();
        handler.setAttribute("velocityTemplateEngine", velocityEngine);
        handler.setAttribute("threadSafeHotelData", threadSafeHotelDataBuilder);

        ResourceHandler resource_handler = new ResourceHandler();
        resource_handler.setDirectoriesListed(true);
        resource_handler.setResourceBase("static");

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[]{resource_handler, handler});
        server.setHandler(handlers);

        try {
            server.start();
            server.join();
        } catch (Exception ex) {
            LogHelper.getLogger().error(ex);
        }
    }
}