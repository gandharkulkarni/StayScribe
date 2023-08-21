package hotelappserver;

/**
 * Class to store database queries in PreparedStatement format
 * by:Gandhar Kulkarni
 */
public class PreparedStatements {
    public static final String CREATE_USER_TABLE = "CREATE TABLE users (" +
            "userid INTEGER AUTO_INCREMENT PRIMARY KEY, " +
            "username VARCHAR(32) NOT NULL UNIQUE, " +
            "password CHAR(64) NOT NULL, " +
            "user_salt CHAR(32) NOT NULL);";
    public static final String CREATE_HOTELS_TABLE = "CREATE TABLE hotels (" +
            "hotel_id varchar(64) PRIMARY KEY, " +
            "hotel_name VARCHAR(64) NOT NULL, " +
            "hotel_address VARCHAR(64) NOT NULL, " +
            "hotel_lat DOUBLE NOT NULL, " +
            "hotel_long DOUBLE NOT NULL, " +
            "hotel_city VARCHAR(64) NOT NULL, " +
            "hotel_state VARCHAR(64) NOT NULL, " +
            "hotel_country VARCHAR(64) NOT NULL);";
    public static final String CREATE_REVIEWS_TABLE = "CREATE TABLE reviews(" +
            "review_id VARCHAR(64), " +
            "review_rating DOUBLE NOT NULL," +
            "review_title VARCHAR(64) NOT NULL, " +
            "review_text VARCHAR(2000) NOT NULL, " +
            "review_user VARCHAR(64) NOT NULL, " +
            "review_submission_date VARCHAR(64) NOT NULL, " +
            "hotel_id  VARCHAR(64) NOT NULL, " +
            "PRIMARY KEY(review_id, hotel_id), " +
            "FOREIGN KEY(hotel_id) REFERENCES hotels(hotel_id));";
    public static final String CREATE_USER_EXPEDIA_VISIT_TABLE = "CREATE TABLE user_expedia_visits(" +
            "user_id varchar(64) NOT NULL, " +
            "hotel_id varchar(64) NOT NULL, " +
            "hotel_name varchar(64) NOT NULL, "+
            "hotel_link varchar(1000) NOT NULL, " +
            "visit_count int NOT NULL, " +
            "PRIMARY KEY(user_id, hotel_id));";
    public static final String CREATE_USER_LOGIN_HISTORY_TABLE = "CREATE TABLE user_login_history(" +
            "user_id VARCHAR(64) PRIMARY KEY, " +
            "last_login VARCHAR(64) NOT NULL " +
            "); ";
    public static final String CREATE_USER_FAVOURITE_HOTEL_TABLE = "CREATE TABLE user_favourite_hotels(" +
            "user_id VARCHAR(64) NOT NULL, " +
            "hotel_id VARCHAR(64) NOT NULL, "+
            "PRIMARY KEY(user_id,hotel_id));";
    public static final String INSERT_INTO_FAVOURITE_HOTEL_TABLE = "INSERT INTO user_favourite_hotels (user_id,hotel_id) VALUES (?,?);";
    public static final String GET_USER_FAVOURITE_HOTELS = "SELECT f.hotel_id as hotel_id , h.hotel_name as hotel_name FROM user_favourite_hotels f INNER JOIN hotels h on f.hotel_id=h.hotel_id WHERE f.user_id=?;";
    public static final String CHECK_USER_FAVOURITE_HOTEL = "SELECT * FROM user_favourite_hotels WHERE user_id=? and hotel_id=?;";
    public static final String DELETE_USER_FAVOURITE_HOTELS = "DELETE FROM user_favourite_hotels WHERE user_id=?;";
    public static final String GET_LAST_LOGIN_DETAILS = "SELECT * from user_login_history WHERE user_id=?;";

    public static final String INSERT_LAST_LOGIN_INFORMATION = "INSERT INTO user_login_history (user_id, last_login) VALUES(?,?);";
    public static final String UPDATE_LAST_LOGIN_DETAILS = "UPDATE user_login_history SET last_login=? WHERE user_id=?";
    public static final String INSERT_HOTEL_DETAILS = "INSERT INTO hotels (hotel_id, hotel_name, hotel_address, hotel_lat, hotel_long, hotel_city, hotel_state, hotel_country) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?, ?);";
    public static final String INSERT_REVIEW_DETAILS = "INSERT INTO reviews(review_id, review_rating, review_title, review_text, review_user, review_submission_date, hotel_id) " +
            "VALUES (?, ?, ?, ?, ?, ?, ?);";
    public static final String GET_ALL_HOTELS = "SELECT * FROM hotels;";

    public static final String GET_HOTEL_BY_HOTEL_ID = "SELECT * FROM hotels WHERE hotel_id=?;";

    public static final String GET_REVIEWS_BY_HOTEL_ID = "SELECT * FROM reviews WHERE hotel_id=?;";

    public static final String GET_AVERAGE_RATING_FOR_HOTEL = "SELECT AVG(review_rating) as averageRating FROM reviews WHERE hotel_id=?;";

    public static final String GET_LIMITED_REVIEWS_BY_HOTEL_ID = "SELECT * FROM reviews WHERE hotel_id=? order by review_submission_date desc limit ? offset ?;";

    public static final String GET_USER_EXPEDIA_VISIT_COUNT_BY_HOTEL_ID = "SELECT visit_count FROM user_expedia_visits where user_id=? and hotel_id=?";

    public static final String UPDATE_USER_EXPEDIA_VISIT_COUNT_BY_HOTEL_ID = "UPDATE user_expedia_visits SET visit_count=visit_count+1 where user_id=? and hotel_id=?";

    public static final String INSERT_USER_EXPEDIA_VISIT_COUNT_BY_HOTEL_ID = "INSERT INTO user_expedia_visits (user_id, hotel_id, hotel_name, hotel_link, visit_count) VALUES (?, ?, ?, ?, ?)";

    public static final String CHECK_USERNAME_EXISTS_SQL = "SELECT username FROM users WHERE username = ?";

    public static final String SHOW_TABLES_SQL = "SHOW TABLES;";

    public static final String GET_ALL_REVIEW_ID = "SELECT review_id FROM reviews;";
    public static final String UPDATE_REVIEW_DETAILS = "UPDATE reviews SET review_title=?, review_text=?, review_rating=?, review_submission_date=?  WHERE review_id=? and hotel_id=? and review_user=?;";
    public static final String DELETE_REVIEW = "DELETE FROM reviews where review_id=? and hotel_id=?;";

    public static final String REGISTER_USER_SQL =
            "INSERT INTO users (username, password, user_salt) VALUES (?, ?, ?);";

    public static final String GET_USER_SALT_SQL =
            "SELECT user_salt FROM users WHERE username = ?";

    public static final String AUTHENTICATE_USER_SQL =
            "SELECT username FROM users " +
                    "WHERE username = ? AND password = ?";

    public static final String GET_USER_EXPEDIA_VISIT_HISTORY = "SELECT * FROM user_expedia_visits WHERE user_id=? order by visit_count desc;";

    public static final String DELETE_USER_EXPEDIA_VISIT_HISTORY = "DELETE FROM user_expedia_visits WHERE user_id=?;";
}
