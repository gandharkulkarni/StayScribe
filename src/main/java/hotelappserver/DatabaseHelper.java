package hotelappserver;

import hotelapp.*;

import java.io.FileReader;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.sql.*;
import java.util.*;

/**
 * Class to manage all database relation operations
 * by: Gandhar Kulkarni
 */
public class DatabaseHelper {
    private static DatabaseHelper databaseHelper = new DatabaseHelper("database.properties"); // singleton pattern
    private Properties config; // a "map" of properties
    private String uri = null; // uri to connect to mysql using jdbc
    private Random random = new Random(); // used in password  generation

    /**
     * DataBaseHandler is a singleton, we want to prevent other classes
     * from creating objects of this class using the constructor
     */
    private DatabaseHelper(String propertiesFile) {
        this.config = loadConfigFile(propertiesFile);
        this.uri = "jdbc:mysql://" + config.getProperty("hostname") + "/" + config.getProperty("username") + "?useUnicode=true&useJDBCCompliantTimezoneShift=true&useLegacyDatetimeCode=false&serverTimezone=UTC";
        //System.out.println("uri = " + uri);
    }

    /**
     * Returns the instance of the database handler.
     *
     * @return instance of the database handler
     */
    public static DatabaseHelper getInstance() {
        return databaseHelper;
    }

    /**
     * Returns List of hotels
     *
     * @return SortedSet<Hotel>
     */
    public SortedSet<Hotel> getAllHotels() {
        SortedSet<Hotel> hotelSet = new TreeSet<>(new Comparator<Hotel>() {
            @Override
            public int compare(Hotel hotelOne, Hotel hotelTwo) {
                return hotelOne.getHotelId().compareTo(hotelTwo.getHotelId());
            }
        });
        PreparedStatement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            try {
                statement = dbConnection.prepareStatement(PreparedStatements.GET_ALL_HOTELS);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    String hotelId = resultSet.getString("hotel_id");
                    String hotelName = resultSet.getString("hotel_name");
                    String hotelAddress = resultSet.getString("hotel_address");
                    Double hotelLatitude = Double.parseDouble(resultSet.getString("hotel_lat"));
                    Double hotelLongitude = Double.parseDouble(resultSet.getString("hotel_long"));
                    String hotelCity = resultSet.getString("hotel_city");
                    String hotelState = resultSet.getString("hotel_state");
                    String hotelCountry = resultSet.getString("hotel_country");
                    hotelSet.add(
                            new Hotel(hotelId, hotelName, hotelAddress, new HotelCoordinates(hotelLatitude, hotelLongitude), hotelCity, hotelState, hotelCountry)
                    );
                }
                statement.close();
            } catch (Exception ex) {
                LogHelper.getLogger().error(ex);
                hotelSet = null;
            }
        } catch (SQLException ex) {
            LogHelper.getLogger().error(ex);
            hotelSet = null;
        }
        return hotelSet;
    }

    /**
     * Returns Hotel object for hotelId
     *
     * @param hotelId String
     * @return Hotel
     */
    public Hotel getHotelByHotelId(String hotelId) {
        PreparedStatement statement;
        Hotel hotel = null;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            try {
                statement = dbConnection.prepareStatement(PreparedStatements.GET_HOTEL_BY_HOTEL_ID);
                statement.setString(1, hotelId);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    hotelId = resultSet.getString("hotel_id");
                    String hotelName = resultSet.getString("hotel_name");
                    String hotelAddress = resultSet.getString("hotel_address");
                    Double hotelLatitude = Double.parseDouble(resultSet.getString("hotel_lat"));
                    Double hotelLongitude = Double.parseDouble(resultSet.getString("hotel_long"));
                    String hotelCity = resultSet.getString("hotel_city");
                    String hotelState = resultSet.getString("hotel_state");
                    String hotelCountry = resultSet.getString("hotel_country");
                    hotel = new Hotel(hotelId, hotelName, hotelAddress, new HotelCoordinates(hotelLatitude, hotelLongitude), hotelCity, hotelState, hotelCountry);
                }
                statement.close();
            } catch (Exception ex) {
                LogHelper.getLogger().error(ex);
                hotel = null;
            }
        } catch (SQLException ex) {
            LogHelper.getLogger().error(ex);
            hotel = null;
        }
        return hotel;
    }

    /**
     * Returns the list of reviews for given hotelId
     *
     * @param hotelId String
     * @return SortedSet<Review>
     */
    public SortedSet<Review> getReviewByHotelId(String hotelId) {
        SortedSet<Review> reviewSet = new TreeSet<>(new ReviewByDateComparator());
        PreparedStatement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            try {
                statement = dbConnection.prepareStatement(PreparedStatements.GET_REVIEWS_BY_HOTEL_ID);
                statement.setString(1, hotelId);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    String reviewId = resultSet.getString("review_id");
                    Double reviewRating = resultSet.getDouble("review_rating");
                    String reviewTitle = resultSet.getString("review_title");
                    String reviewText = resultSet.getString("review_text");
                    String reviewUser = resultSet.getString("review_user");
                    String reviewSubmissionDate = resultSet.getString("review_submission_date") + "T00:00:00Z";
                    hotelId = resultSet.getString("hotel_id");
                    reviewSet.add(
                            new Review(hotelId, reviewId, reviewRating, reviewTitle, reviewText, reviewUser, reviewSubmissionDate)
                    );
                }
            } catch (Exception ex) {
                LogHelper.getLogger().error(ex);
                reviewSet = null;
            }
        } catch (SQLException ex) {
            LogHelper.getLogger().error(ex);
            reviewSet = null;
        }
        return reviewSet;

    }

    /**
     * Returns average rating for a hotel
     *
     * @param hotelId String
     * @return Double
     */
    public double getAverageRatingForHotel(String hotelId) {
        Double reviewRating = 0.0;
        PreparedStatement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            try {
                statement = dbConnection.prepareStatement(PreparedStatements.GET_AVERAGE_RATING_FOR_HOTEL);
                statement.setString(1, hotelId);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    reviewRating = resultSet.getDouble("averageRating");
                }
            } catch (Exception ex) {
                LogHelper.getLogger().error(ex);
            }
        } catch (SQLException ex) {
            LogHelper.getLogger().error(ex);
        }
        return reviewRating;
    }

    /**
     * Returns set of reviews for a given hotelId according to provided limit and offset.
     *
     * @param hotelId String
     * @param limit   String
     * @param offset  String
     * @return SortedSet<Review>
     */
    public SortedSet<Review> getLimitedReviewByHotelId(String hotelId, String limit, String offset) {
        SortedSet<Review> reviewSet = new TreeSet<>(new ReviewByDateComparator());
        PreparedStatement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            try {
                statement = dbConnection.prepareStatement(PreparedStatements.GET_LIMITED_REVIEWS_BY_HOTEL_ID);
                statement.setString(1, hotelId);
                statement.setInt(2, Integer.parseInt(limit));
                statement.setInt(3, Integer.parseInt(offset));
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    String reviewId = resultSet.getString("review_id");
                    Double reviewRating = resultSet.getDouble("review_rating");
                    String reviewTitle = resultSet.getString("review_title");
                    String reviewText = resultSet.getString("review_text");
                    String reviewUser = resultSet.getString("review_user");
                    String reviewSubmissionDate = resultSet.getString("review_submission_date") + "T00:00:00Z";
                    hotelId = resultSet.getString("hotel_id");
                    reviewSet.add(
                            new Review(hotelId, reviewId, reviewRating, reviewTitle, reviewText, reviewUser, reviewSubmissionDate)
                    );
                }
            } catch (Exception ex) {
                LogHelper.getLogger().error(ex);
                reviewSet = null;
            }
        } catch (SQLException ex) {
            LogHelper.getLogger().error(ex);
            reviewSet = null;
        }
        return reviewSet;

    }

    /**
     * Returns set of reviewIds
     *
     * @return Set<String>
     */
    public Set<String> getAllReviewId() {
        Set<String> reviewIdSet = new HashSet<>();
        PreparedStatement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            try {
                statement = dbConnection.prepareStatement(PreparedStatements.GET_ALL_REVIEW_ID);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    String reviewId = resultSet.getString("review_id");
                    reviewIdSet.add(reviewId);
                }
            } catch (Exception ex) {
                LogHelper.getLogger().error(ex);
                reviewIdSet = null;
            }
        } catch (SQLException ex) {
            LogHelper.getLogger().error(ex);
            reviewIdSet = null;
        }
        return reviewIdSet;
    }

    /**
     * Returns Expedia visit link history for a user
     *
     * @param username String
     * @return List<List < String>>
     */
    public List<List<String>> getExpediaVisitHistory(String username) {
        List<List<String>> visitHistory = new ArrayList<>();
        PreparedStatement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            try {
                statement = dbConnection.prepareStatement(PreparedStatements.GET_USER_EXPEDIA_VISIT_HISTORY);
                statement.setString(1, username);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    String hotelName = resultSet.getString("hotel_name");
                    String hotelLink = resultSet.getString("hotel_link");
                    String visitCount = resultSet.getString("visit_count");
                    List<String> hotelPageVisitDetails = new ArrayList<>();
                    hotelPageVisitDetails.add(hotelName);
                    hotelPageVisitDetails.add(hotelLink);
                    hotelPageVisitDetails.add(visitCount);
                    visitHistory.add(hotelPageVisitDetails);
                }

            } catch (Exception ex) {
                LogHelper.getLogger().error(ex);
                visitHistory = null;
            }
        } catch (SQLException ex) {
            LogHelper.getLogger().error(ex);
            visitHistory = null;
        }
        return visitHistory;
    }

    /**
     * Returns visit count of expedia page of a hotel for a user
     *
     * @param username String
     * @param hotelId  String
     * @return int
     */
    public int getExpediaPageVisitCount(String username, String hotelId) {
        PreparedStatement statement;
        int visitCount = 0;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            try {
                statement = dbConnection.prepareStatement(PreparedStatements.GET_USER_EXPEDIA_VISIT_COUNT_BY_HOTEL_ID);
                statement.setString(1, username);
                statement.setString(2, hotelId);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    visitCount = resultSet.getInt("visit_count");
                }
            } catch (Exception ex) {
                LogHelper.getLogger().error(ex);
            }
        } catch (SQLException ex) {
            LogHelper.getLogger().error(ex);
        }
        return visitCount;
    }

    /**
     * Return names of tables in the database
     *
     * @return HashSet<String>
     */
    public HashSet<String> getExistingTables() {
        PreparedStatement statement;
        HashSet<String> existingTables = new HashSet<>();
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.SHOW_TABLES_SQL);

            ResultSet results = statement.executeQuery();
            ResultSetMetaData rsmd = results.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (results.next()) {
                for (int i = 0; i < columnsNumber; i++)
                    existingTables.add(results.getString(i + 1));
            }
            return existingTables;
        } catch (SQLException ex) {
            LogHelper.getLogger().error(ex);
        }
        return null;
    }

    /**
     * Updates the review details in database
     *
     * @param review Review
     */
    public void updateReviewDetails(Review review) {
        PreparedStatement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            try {
                statement = dbConnection.prepareStatement(PreparedStatements.UPDATE_REVIEW_DETAILS);
                statement.setString(1, review.getReviewTitle());
                statement.setString(2, review.getReviewText());
                statement.setDouble(3, review.getOverallRating());
                statement.setString(4, review.getReviewSubmissionDate().toString());
                statement.setString(5, review.getReviewId());
                statement.setString(6, review.getHotelId());
                statement.setString(7, review.getUserNickName());
                statement.executeUpdate();
                statement.close();
            } catch (Exception ex) {
                LogHelper.getLogger().error(ex);
            }
        } catch (SQLException ex) {
            LogHelper.getLogger().error(ex);
        }
    }

    /**
     * Deletes the review from database
     *
     * @param review Review
     */
    public void deleteReview(Review review) {
        PreparedStatement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            try {
                statement = dbConnection.prepareStatement(PreparedStatements.DELETE_REVIEW);
                statement.setString(1, review.getReviewId());
                statement.setString(2, review.getHotelId());
                statement.executeUpdate();
                statement.close();
            } catch (Exception ex) {
                LogHelper.getLogger().error(ex);
            }
        } catch (SQLException ex) {
            LogHelper.getLogger().error(ex);
        }
    }

    /**
     * Creates User table
     */
    public void createUsersTable() {
        Statement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = dbConnection.createStatement();
            statement.executeUpdate(PreparedStatements.CREATE_USER_TABLE);
        } catch (SQLException ex) {
            LogHelper.getLogger().error(ex);
        }
    }

    /**
     * Creates Expedia visit history table
     */
    public void createExpediaVisitHistoryTable() {
        Statement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = dbConnection.createStatement();
            statement.executeUpdate(PreparedStatements.CREATE_USER_EXPEDIA_VISIT_TABLE);
        } catch (SQLException ex) {
            LogHelper.getLogger().error(ex);
        }
    }

    /**
     * Creates hotels table
     */
    public void createHotelsTable() {
        Statement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = dbConnection.createStatement();
            statement.executeUpdate(PreparedStatements.CREATE_HOTELS_TABLE);
        } catch (SQLException ex) {
            LogHelper.getLogger().error(ex);
        }
    }

    /**
     * Updates visit count of a expedia page for a user
     *
     * @param username String
     * @param hotelId  String
     */
    public void updateExpediaPageVisitCount(String username, String hotelId) {
        PreparedStatement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            try {
                statement = dbConnection.prepareStatement(PreparedStatements.UPDATE_USER_EXPEDIA_VISIT_COUNT_BY_HOTEL_ID);
                statement.setString(1, username);
                statement.setString(2, hotelId);
                statement.executeUpdate();
                statement.close();
            } catch (Exception ex) {
                LogHelper.getLogger().error(ex);
            }
        } catch (SQLException ex) {
            LogHelper.getLogger().error(ex);
        }
    }

    /**
     * Inserts a new record into Expedia visit history table
     *
     * @param username  String
     * @param hotelId   String
     * @param hotelName String
     * @param hotelLink String
     * @return int
     */
    public void insertExpediaPageVisitCount(String username, String hotelId, String hotelName, String hotelLink) {
        PreparedStatement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            try {
                statement = dbConnection.prepareStatement(PreparedStatements.INSERT_USER_EXPEDIA_VISIT_COUNT_BY_HOTEL_ID);
                statement.setString(1, username);
                statement.setString(2, hotelId);
                statement.setString(3, hotelName);
                statement.setString(4, hotelLink);
                statement.setInt(5, 1);
                statement.executeUpdate();
                statement.close();
            } catch (Exception ex) {
                LogHelper.getLogger().error(ex);
            }
        } catch (SQLException ex) {
            LogHelper.getLogger().error(ex);
        }
    }

    /**
     * Deletes expedia visit history records for a user
     *
     * @param username String
     */
    public void clearExpediaVisitHistory(String username) {
        PreparedStatement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            try {
                statement = dbConnection.prepareStatement(PreparedStatements.DELETE_USER_EXPEDIA_VISIT_HISTORY);
                statement.setString(1, username);
                statement.executeUpdate();
                statement.close();
            } catch (Exception ex) {
                LogHelper.getLogger().error(ex);
            }
        } catch (SQLException ex) {
            LogHelper.getLogger().error(ex);
        }

    }

    /**
     * Insert new record into Hotels table
     *
     * @param hotelList List<Hotel>
     */
    public void insertRecordIntoHotelsTable(List<Hotel> hotelList) {
        PreparedStatement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            for (Hotel hotel : hotelList) {
                try {
                    statement = dbConnection.prepareStatement(PreparedStatements.INSERT_HOTEL_DETAILS);
                    statement.setString(1, hotel.getHotelId());
                    statement.setString(2, hotel.getHotelName());
                    statement.setString(3, hotel.getHotelAddress());
                    statement.setDouble(4, hotel.getHotelCoordinates().getHotelLatitude());
                    statement.setDouble(5, hotel.getHotelCoordinates().getHotelLongitude());
                    statement.setString(6, hotel.getHotelCity());
                    statement.setString(7, hotel.getHotelState());
                    statement.setString(8, hotel.getCountry());
                    statement.executeUpdate();
                    statement.close();
                } catch (Exception ex) {
                    LogHelper.getLogger().error(ex);
                }
            }
        } catch (SQLException ex) {
            LogHelper.getLogger().error(ex);
        }
    }

    /**
     * Creates Reviews table
     */
    public void createReviewsTable() {
        Statement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = dbConnection.createStatement();
            statement.executeUpdate(PreparedStatements.CREATE_REVIEWS_TABLE);
        } catch (SQLException ex) {
            LogHelper.getLogger().error(ex);
        }
    }

    /**
     * Creates user login history table
     */
    public void createUserHistoryTable() {
        Statement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = dbConnection.createStatement();
            statement.executeUpdate(PreparedStatements.CREATE_USER_LOGIN_HISTORY_TABLE);
        } catch (SQLException ex) {
            LogHelper.getLogger().error(ex);
        }
    }

    /**
     * Creates user favourite hotels table
     */
    public void createUserFavouriteHotelsTable() {
        Statement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = dbConnection.createStatement();
            statement.executeUpdate(PreparedStatements.CREATE_USER_FAVOURITE_HOTEL_TABLE);
        } catch (SQLException ex) {
            LogHelper.getLogger().error(ex);
        }
    }

    /**
     * Returns last login time for a user
     *
     * @param username String
     * @return String
     */
    public String getLastLoginDetails(String username) {
        PreparedStatement statement;
        String lastLogin = "N/A";
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            try {
                statement = dbConnection.prepareStatement(PreparedStatements.GET_LAST_LOGIN_DETAILS);
                statement.setString(1, username);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    lastLogin = resultSet.getString("last_login");
                }
            } catch (Exception ex) {
                LogHelper.getLogger().error(ex);
            }
        } catch (SQLException ex) {
            LogHelper.getLogger().error(ex);
        }
        return lastLogin;
    }

    /**
     * Get list of favourite hotels
     *
     * @param username String
     * @return String
     */
    public List<List<String>> getFavouriteHotels(String username) {
        List<List<String>> favouriteHotels = new ArrayList<>();
        PreparedStatement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            try {
                statement = dbConnection.prepareStatement(PreparedStatements.GET_USER_FAVOURITE_HOTELS);
                statement.setString(1, username);
                ResultSet resultSet = statement.executeQuery();
                while (resultSet.next()) {
                    String hotelId = resultSet.getString("hotel_id");
                    String hotelName = resultSet.getString("hotel_name");
                    List<String> hotelDetails = new ArrayList<>();
                    hotelDetails.add(hotelId);
                    hotelDetails.add(hotelName);
                    favouriteHotels.add(hotelDetails);
                }

            } catch (Exception ex) {
                LogHelper.getLogger().error(ex);
                favouriteHotels = null;
            }
        } catch (SQLException ex) {
            LogHelper.getLogger().error(ex);
            favouriteHotels = null;
        }
        return favouriteHotels;
    }

    /**
     * Inserts a new record in favourite hotels table
     *
     * @param username String
     * @param hotelId  String
     * @return boolean
     */
    public boolean insertFavouriteHotelDetails(String username, String hotelId) {
        PreparedStatement statement;
        boolean returnValue = false;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            try {
                statement = dbConnection.prepareStatement(PreparedStatements.INSERT_INTO_FAVOURITE_HOTEL_TABLE);
                statement.setString(1, username);
                statement.setString(2, hotelId);
                statement.executeUpdate();
                statement.close();
                returnValue = true;
            } catch (Exception ex) {
                LogHelper.getLogger().error(ex);
                returnValue = false;
            }
        } catch (SQLException ex) {
            LogHelper.getLogger().error(ex);
            returnValue = false;
        }
        return returnValue;
    }

    /**
     * Checks if favourite hotels already exists
     *
     * @param username String
     * @param hotelId  String
     * @return boolean
     */
    public boolean checkFavouriteHotelDetailsExists(String username, String hotelId) {
        PreparedStatement statement;
        boolean returnValue = false;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            try {
                statement = dbConnection.prepareStatement(PreparedStatements.CHECK_USER_FAVOURITE_HOTEL);
                statement.setString(1, username);
                statement.setString(2, hotelId);
                ResultSet resultSet = statement.executeQuery();
                returnValue = resultSet.next();
                statement.close();
            } catch (Exception ex) {
                LogHelper.getLogger().error(ex);
                returnValue = false;
            }
        } catch (SQLException ex) {
            LogHelper.getLogger().error(ex);
            returnValue = false;
        }
        return returnValue;
    }

    /**
     * Deletes favourite hotels for user
     *
     * @param username String
     */
    public void clearFavouriteHotels(String username) {
        PreparedStatement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            try {
                statement = dbConnection.prepareStatement(PreparedStatements.DELETE_USER_FAVOURITE_HOTELS);
                statement.setString(1, username);
                statement.executeUpdate();
                statement.close();
            } catch (Exception ex) {
                LogHelper.getLogger().error(ex);
            }
        } catch (SQLException ex) {
            LogHelper.getLogger().error(ex);
        }
    }

    /**
     * Inserts last login details into database
     *
     * @param username  String
     * @param lastLogin String
     */
    public void insertLastLoginDetails(String username, String lastLogin) {
        PreparedStatement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            try {
                statement = dbConnection.prepareStatement(PreparedStatements.INSERT_LAST_LOGIN_INFORMATION);
                statement.setString(1, username);
                statement.setString(2, lastLogin);
                statement.executeUpdate();
            } catch (Exception ex) {
                LogHelper.getLogger().error(ex);
            }
        } catch (SQLException ex) {
            LogHelper.getLogger().error(ex);
        }
    }

    /**
     * Updates last login details for a user
     *
     * @param username  String
     * @param lastLogin String
     */
    public void updateLastLoginDetails(String username, String lastLogin) {
        PreparedStatement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            try {
                statement = dbConnection.prepareStatement(PreparedStatements.UPDATE_LAST_LOGIN_DETAILS);
                statement.setString(1, lastLogin);
                statement.setString(2, username);
                statement.executeUpdate();
            } catch (Exception ex) {
                LogHelper.getLogger().error(ex);
            }
        } catch (SQLException ex) {
            LogHelper.getLogger().error(ex);
        }
    }

    /**
     * Inserts review into database
     *
     * @param reviewList List<Review>
     */
    public void insertRecordIntoReviewsTable(List<Review> reviewList) {
        PreparedStatement statement;
        try (Connection dbConnection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            for (Review review : reviewList) {
                try {
                    statement = dbConnection.prepareStatement(PreparedStatements.INSERT_REVIEW_DETAILS);
                    statement.setString(1, review.getReviewId());
                    statement.setDouble(2, review.getOverallRating());
                    statement.setString(3, review.getReviewTitle());
                    statement.setString(4, review.getReviewText());
                    statement.setString(5, review.getUserNickName());
                    statement.setString(6, review.getReviewSubmissionDate().toString());
                    statement.setString(7, review.getHotelId());
                    statement.executeUpdate();
                    statement.close();
                } catch (Exception ex) {
                    LogHelper.getLogger().error(ex);
                }
            }
        } catch (SQLException ex) {
            LogHelper.getLogger().error(ex);
        }
    }

    /**
     * Attempts to load properties file with database configuration. Must
     * include username, password, database, and hostname.
     *
     * @param propertyFile path to database properties file
     * @return database properties
     */
    public Properties loadConfigFile(String propertyFile) {
        Properties config = new Properties();
        try (FileReader fr = new FileReader(propertyFile)) {
            config.load(fr);
        } catch (IOException ex) {
            LogHelper.getLogger().error(ex);
        }

        return config;
    }

    /**
     * Returns the hex encoding of a byte array.
     *
     * @param bytes  - byte array to encode
     * @param length - desired length of encoding
     * @return hex encoded byte array
     */
    public static String encodeHex(byte[] bytes, int length) {
        BigInteger bigint = new BigInteger(1, bytes);
        String hex = String.format("%0" + length + "X", bigint);

        assert hex.length() == length;
        return hex;
    }

    /**
     * Calculates the hash of a password and salt using SHA-256.
     *
     * @param password - password to hash
     * @param salt     - salt associated with user
     * @return hashed password
     */
    public static String getHash(String password, String salt) {
        String salted = salt + password;
        String hashed = salted;

        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            md.update(salted.getBytes());
            hashed = encodeHex(md.digest(), 64);
        } catch (Exception ex) {
            LogHelper.getLogger().error(ex);
        }

        return hashed;
    }

    /**
     * Registers a new user, placing the username, password hash, and
     * salt into the database.
     *
     * @param newuser - username of new user
     * @param newpass - password of new user
     */
    public void registerUser(String newuser, String newpass) {
        // Generate salt
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);

        String usersalt = encodeHex(saltBytes, 32); // salt
        String passhash = getHash(newpass, usersalt); // hashed password

        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            try {
                statement = connection.prepareStatement(PreparedStatements.REGISTER_USER_SQL);
                statement.setString(1, newuser);
                statement.setString(2, passhash);
                statement.setString(3, usersalt);
                statement.executeUpdate();
                statement.close();
            } catch (SQLException ex) {
                LogHelper.getLogger().error(ex);
            }
        } catch (SQLException ex) {
            LogHelper.getLogger().error(ex);
        }
    }

    /**
     * Authenticate user credentials
     *
     * @param username String
     * @param password String
     * @return boolean
     */

    public boolean authenticateUser(String username, String password) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.AUTHENTICATE_USER_SQL);
            String usersalt = getSalt(connection, username);
            String passhash = getHash(password, usersalt);

            statement.setString(1, username);
            statement.setString(2, passhash);
            ResultSet results = statement.executeQuery();
            boolean flag = results.next();
            return flag;
        } catch (SQLException ex) {
            LogHelper.getLogger().error(ex);
        }
        return false;
    }

    /**
     * Method to check if username is already exists in database
     *
     * @param username
     * @return
     */
    public boolean checkUsernameAlreadyExists(String username) {
        PreparedStatement statement;
        try (Connection connection = DriverManager.getConnection(uri, config.getProperty("username"), config.getProperty("password"))) {
            statement = connection.prepareStatement(PreparedStatements.CHECK_USERNAME_EXISTS_SQL);

            statement.setString(1, username);
            ResultSet results = statement.executeQuery();
            boolean flag = results.next();
            return flag;
        } catch (SQLException ex) {
            LogHelper.getLogger().error(ex);
        }
        return false;
    }

    /**
     * Gets the salt for a specific user.
     *
     * @param connection - active database connection
     * @param user       - which user to retrieve salt for
     * @return salt for the specified user or null if user does not exist
     * @throws SQLException if any issues with database connection
     */
    private String getSalt(Connection connection, String user) {
        String salt = null;
        try (PreparedStatement statement = connection.prepareStatement(PreparedStatements.GET_USER_SALT_SQL)) {
            statement.setString(1, user);
            ResultSet results = statement.executeQuery();
            if (results.next()) {
                salt = results.getString("user_salt");
                return salt;
            }
        } catch (SQLException ex) {
            LogHelper.getLogger().error(ex);
        }
        return salt;
    }
}
