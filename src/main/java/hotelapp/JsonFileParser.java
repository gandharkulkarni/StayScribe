package hotelapp;

import com.google.gson.*;
import com.google.gson.reflect.TypeToken;

import java.io.FileReader;
import java.lang.reflect.Type;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/**
 * This class has methods to parse Json files.
 * By: Gandhar Kulkarni
 */
public class JsonFileParser {

    /**
     * Parse hotel json file
     * @param path Path of Hotel Json file
     * @return Boolean - True if parsing is successful otherwise false
     */
    public static List<Hotel> parseHotelJsonFile(Path path)
    {
        Gson gsonObj = new Gson();
        List<Hotel> hotels = null;
        try(FileReader fileReader = new FileReader(path.toString()))
        {
            JsonParser parser = new JsonParser();
            JsonObject jo = (JsonObject) parser.parse(fileReader);
            JsonArray jsonArr = jo.getAsJsonArray("sr");
            Type hotelType = new TypeToken<ArrayList<Hotel>>(){}.getType();
            hotels = gsonObj.fromJson(jsonArr, hotelType);

        }
        catch (Exception ex){
            LogHelper.getLogger().error(ex);
            hotels=null;
        }
        return hotels;
    }

    /**
     * Parse review json file
     * @param path Path of review Json file
     * @return Boolean - True if parsing is successful otherwise false
     */
    public static List<Review> parseReviewJsonFile(Path path)
    {
        Gson gsonObj = new Gson();
        boolean returnValue=false;
        List<Review> reviews = null;
        try(FileReader fileReader = new FileReader(path.toString()))
        {
            JsonParser parser = new JsonParser();
            JsonObject jo = (JsonObject) parser.parse(fileReader);
            JsonArray jsonArr = jo.getAsJsonObject("reviewDetails").getAsJsonObject("reviewCollection").getAsJsonArray("review");
            reviews = new ArrayList<>();
            for(JsonElement element: jsonArr){
                JsonObject jsonReviewObj = element.getAsJsonObject();
                String hotelId =jsonReviewObj.get("hotelId").getAsString();
                String reviewId = jsonReviewObj.get("reviewId").getAsString();
                double overallRating = jsonReviewObj.get("ratingOverall").getAsDouble();
                String reviewTitle= jsonReviewObj.get("title").getAsString();
                String reviewText = jsonReviewObj.get("reviewText").getAsString();
                String userNickName = jsonReviewObj.get("userNickname").getAsString();
                String reviewSubmissionDate = jsonReviewObj.get("reviewSubmissionTime").getAsString();

                Review newReview = new Review(hotelId,reviewId,overallRating,reviewTitle,reviewText,userNickName,reviewSubmissionDate);
                reviews.add(newReview);
            }
        }
        catch (Exception ex){
            LogHelper.getLogger().error(ex);
            reviews = null;
        }
        return reviews;
    }
}
