package hotelapp;

import hotelappserver.DatabaseHelper;
import hotelappserver.HotelServer;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.Set;

/** The driver class for project 4
 * Copy your project 3 classes to the hotelapp package.
 * The main function should take the following command line arguments:
 * -hotels hotelFile -reviews reviewsDirectory -threads numThreads -output filepath
 * (order may be different)
 * and read general information about the hotels from the hotelFile (a JSON file),
 * as read hotel reviews from the json files in reviewsDirectory (can be multithreaded or
 * single-threaded).
 * The data should be loaded into data structures that allow efficient search.
 * If the -output flag is provided, hotel information (about all hotels and reviews)
 * should be output into the given file.
 * Then in the main method, you should start an HttpServer that responds to
 * requests about hotels and reviews.
 * See pdf description of the project for the requirements.
 * You are expected to add other classes and methods from project 3 to this project,
 * and take instructor's / TA's feedback from a code review into account.
 * Please download the input folder from Canvas.
 */
public class HotelSearch {
    public static void main(String[] args) {
        ThreadSafeHotelDataBuilder threadSafeHashMapBuilder = new ThreadSafeHotelDataBuilder();
        boolean hotelsPathFlag = false;
        boolean reviewsPathFlag = false;
        boolean threadCountFlag = false;
        boolean outputPathFlag = false;
        LocalTime time = LocalTime.now();
        Map<String, String> parameterHashMap = new HashMap<>();
        for (int i = 0; i < args.length; i++) {
            if (args[i].equals("-hotels")) {
                parameterHashMap.put(args[i], args[i + 1]);
                i++;
            } else if (args[i].equals("-reviews")) {
                parameterHashMap.put(args[i], args[i + 1]);
                i++;
            } else if (args[i].equals("-threads")) {
                parameterHashMap.put(args[i], args[i + 1]);
                threadCountFlag = true;
                i++;
            } else if (args[i].equals("-output")) {
                parameterHashMap.put(args[i], args[i + 1]);
                outputPathFlag = true;
                i++;
            }
        }
        DirectoryParser parser = null;
        if (threadCountFlag) {
            parser = new DirectoryParser(Integer.parseInt(parameterHashMap.get("-threads")), outputPathFlag, threadSafeHashMapBuilder);
        } else {
            parser = new DirectoryParser(1, outputPathFlag, threadSafeHashMapBuilder);
        }
        DatabaseHelper dbHelper = DatabaseHelper.getInstance();
        Set<String> dbTableSet = dbHelper.getExistingTables();
        if(dbTableSet != null) {
            if (parameterHashMap.containsKey("-hotels") && !dbTableSet.contains("hotels")) {
                System.out.println("Hotels table not found. Creating table...");
                dbHelper.createHotelsTable();
                System.out.println("Adding records to hotels table");
                hotelsPathFlag = parser.parseHotelJsonFile(parameterHashMap.get("-hotels"));
            }
            else{
                hotelsPathFlag = true;
            }
            if (parameterHashMap.containsKey("-reviews") && !dbTableSet.contains("reviews")) {
                System.out.println("Reviews table not found. Creating table...");
                dbHelper.createReviewsTable();
                System.out.println("Adding records to reviews table");
                reviewsPathFlag = parser.searchReviewJsonFileInDirectory(parameterHashMap.get("-reviews"));
                parser.initializeShutdownSequence();
            }
            else {
                reviewsPathFlag = true;
            }
            if(!dbTableSet.contains("users")){
                System.out.println("Users table not found. Creating table...");
                dbHelper.createUsersTable();
            }
            if(!dbTableSet.contains("user_expedia_visits")){
                System.out.println("Expedia visit history table not found. Creating table...");
                dbHelper.createExpediaVisitHistoryTable();
            }
            if(!dbTableSet.contains("user_login_history")){
                System.out.println("User login history table not found. Creating table...");
                dbHelper.createUserHistoryTable();
            }
            if(!dbTableSet.contains("user_favourite_hotels")){
                System.out.println("User favourite hotels table not found. Creating table...");
                dbHelper.createUserFavouriteHotelsTable();
            }
        }
        if (outputPathFlag) {
            OutputFileHelper outputFileHelper = new OutputFileHelper(parameterHashMap.get("-output"), threadSafeHashMapBuilder);
            outputFileHelper.printHotelDetails();
        }

        System.out.print(" Time taken: ");
        System.out.print(time.until(LocalTime.now(), ChronoUnit.MILLIS));
        System.out.println();
        if (hotelsPathFlag && reviewsPathFlag && !outputPathFlag) {
            HotelSearch hotelSearch = new HotelSearch();
            hotelSearch.startJettyServer(threadSafeHashMapBuilder);
            hotelSearch.startEfficientSearch(threadSafeHashMapBuilder);
        }
    }


    /**
     * Starts Jetty server
     *
     * @param threadSafeHashMapBuilder Object
     */
    private void startJettyServer(ThreadSafeHotelDataBuilder threadSafeHashMapBuilder){
        HotelServer server = new HotelServer(threadSafeHashMapBuilder);
        try {
            server.startServer();
        }
        catch (Exception ex){
            LogHelper.getLogger().error(ex);
        }
    }
    /**
     * This method asks for user input through terminal and prints result as per user inputs.
     */
    public void startEfficientSearch(ThreadSafeHotelDataBuilder hashMapBuilder) {
        try {
            Scanner scanner = new Scanner(System.in);
            ThreadSafeEfficientSearch efficientSearch = new ThreadSafeEfficientSearch(hashMapBuilder);
            String input = "";
            String exitSequence = "q";
            while (true) {

                System.out.println();
                System.out.println(" Please choose a search method");
                System.out.println(" 1.find <hotelId> \n 2.findReviews <hotelId> \n 3.findWord <word> \n 4.To Exit press (q)");
                System.out.println("Enter the command: ");

                input = scanner.nextLine();

                if (input.equals(exitSequence)) {
                    System.out.println("Thank you. Have a great day.");
                    System.out.println("*****************************************");
                    break;
                } else {
                    String[] inputParam = input.split(" ");
                    if (inputParam[0].equals("find")) {

                        try {
                            efficientSearch.findHotel(inputParam[1]);
                        } catch (NumberFormatException numberFormatException) {
                            LogHelper.getLogger().error("Invalid input for the command. Please try again.");
                        } catch (Exception ex) {
                            LogHelper.getLogger().error(ex);
                        }

                    } else if (inputParam[0].equals("findReviews")) {

                        try {
                            efficientSearch.findReviews(inputParam[1]);
                        } catch (NumberFormatException numberFormatException) {
                            LogHelper.getLogger().error("Invalid input for the command. Please try again");
                        } catch (Exception ex) {
                            LogHelper.getLogger().error(ex);
                        }

                    } else if (inputParam[0].equals("findWord")) {

                        try {
                            efficientSearch.findWord(inputParam[1]);
                        } catch (Exception ex) {
                            LogHelper.getLogger().error(ex);
                        }

                    } else {

                        LogHelper.getLogger().error("Invalid input. Please enter a command from given option");

                    }
                }
            }
        } catch (Exception ex) {
            LogHelper.getLogger().error(ex);
            LogHelper.getLogger().error("Something went wrong. Please try again after some time");
        }
    }

}
