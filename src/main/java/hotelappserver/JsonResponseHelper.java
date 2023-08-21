package hotelappserver;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import hotelapp.Review;

import java.util.SortedSet;

/**
 * Helper class to get output in Json format
 * By: Gandhar Kulkarni
 */
public class JsonResponseHelper {
    /**
     * Returns review details in Json format
     *
     * @param hotelId String
     * @param limit   String
     * @param offset  String
     * @return JsonObject
     */
    public JsonObject getAllReviewsForHotelInJsonFormat(String hotelId, String limit, String offset) {
        JsonObject reviewInfoResponse = new JsonObject();
        SortedSet<Review> reviewSet = DatabaseHelper.getInstance().getLimitedReviewByHotelId(hotelId, limit, offset);
        if (reviewSet != null) {
            if (reviewSet.size() > 0) {
                reviewInfoResponse.addProperty("success", true);
                reviewInfoResponse.addProperty("hotelId", hotelId);
                JsonArray reviewArray = new JsonArray();
                for (Review review : reviewSet) {
                    JsonObject reviewJson = new JsonObject();
                    reviewJson.addProperty("reviewId", review.getReviewId());
                    reviewJson.addProperty("title", review.getReviewTitle());
                    reviewJson.addProperty("user", review.getUserNickName());
                    reviewJson.addProperty("reviewText", review.getReviewText());
                    reviewJson.addProperty("overallRating", review.getOverallRating());
                    reviewJson.addProperty("date", review.getReviewSubmissionDate().toString());
                    reviewArray.add(reviewJson);
                }
                reviewInfoResponse.add("reviews", reviewArray);
            } else {
                reviewInfoResponse.addProperty("success", false);
                reviewInfoResponse.addProperty("hotelId", "invalid");
            }
        } else {
            reviewInfoResponse.addProperty("success", false);
            reviewInfoResponse.addProperty("hotelId", "invalid");
        }
        return reviewInfoResponse;
    }
}
