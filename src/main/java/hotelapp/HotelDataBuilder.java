package hotelapp;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import java.util.*;

/**
 * Creates hashmap of Hotels Reviews.
 * By: Gandhar Kulkarni
 */
public class HotelDataBuilder {
    private final SortedMap<String, Hotel> hotelDetailsHashMap = new TreeMap<>();
    private final Map<String, SortedSet<Review>> hotelReviewHashMap = new HashMap<>();
    private final Map<String, SortedSet<Review>> wordHashMap = new HashMap<>();
    private final Set<String> reviewIdSet = new HashSet<>();

    /**
     * Returns a set of all valid HotelIds
     *
     * @return Set<String>
     */
    public Set<String> getAllHotelIds() {
        return hotelDetailsHashMap.keySet();
    }

    /**
     * Returns Hotel for given HotelId
     *
     * @param hotelId
     * @return Hotel
     */
    public Hotel getHotelByHotelId(String hotelId) {
        if (hotelDetailsHashMap.containsKey(hotelId)) {
            Hotel hotel = hotelDetailsHashMap.get(hotelId);
            return new Hotel(hotel.getHotelId(),
                    hotel.getHotelName(),
                    hotel.getHotelAddress(),
                    hotel.getHotelCoordinates(),
                    hotel.getHotelCity(),
                    hotel.getHotelState(),
                    hotel.getCountry());
        }
        return null;
    }

    /**
     * Returns set of Reviews of Hotel for given HotelId
     *
     * @param hotelId
     * @return SortedSet<Review>
     */
    public Set<Review> getAllReviewsForHotel(String hotelId) {
        if (hotelReviewHashMap.containsKey(hotelId)) {
            return Collections.unmodifiableSet(hotelReviewHashMap.get(hotelId));
        }
        return null;
    }

    /**
     * Returns a set of reviews containing a specific word
     *
     * @param word
     * @return SortedSet<Review>
     */
    public Set<Review> getReviewsContainingSpecificWord(String word) {
        if (wordHashMap.containsKey(word)) {
            return Collections.unmodifiableSet(wordHashMap.get(word));
        }
        return null;
    }

    /**
     * Returns set of existing review id's
     *
     * @return Set<String>
     */
    public Set<String> getExistingReviewId(){
        return Collections.unmodifiableSet(reviewIdSet);
    }

    /**
     * Returns hotel details in Json format
     *
     * @param hotelId
     * @return JsonObject
     */
    public JsonObject getHotelByHotelIdInJsonFormat(String hotelId) {
        JsonObject hotelInfoResponse = new JsonObject();
        if (this.hotelDetailsHashMap.containsKey(hotelId)) {
            Hotel hotel = this.hotelDetailsHashMap.get(hotelId);
            if (hotel != null) {
                Gson gson = new Gson();
                hotelInfoResponse.addProperty("success", true);
                hotelInfoResponse.addProperty("hotelId", hotel.getHotelId());
                hotelInfoResponse.addProperty("name", hotel.getHotelName());
                hotelInfoResponse.addProperty("addr", hotel.getHotelAddress());
                hotelInfoResponse.addProperty("city", hotel.getHotelCity());
                hotelInfoResponse.addProperty("state", hotel.getHotelState());
                hotelInfoResponse.addProperty("lat", hotel.getHotelCoordinates().getHotelLatitude());
                hotelInfoResponse.addProperty("lng", hotel.getHotelCoordinates().getHotelLongitude());
            }
        } else {
            hotelInfoResponse.addProperty("success", false);
            hotelInfoResponse.addProperty("hotelId", "invalid");
        }
        return hotelInfoResponse;
    }

    /**
     * Returns set of reviews in Json format
     *
     * @param hotelId String
     * @param requestedCount int
     * @return JsonObject
     */
    public JsonObject getAllReviewsForHotelInJsonFormat(String hotelId, int requestedCount) {
        JsonObject reviewInfoResponse = new JsonObject();
        if (this.hotelReviewHashMap.containsKey(hotelId) && requestedCount > 0) {
            SortedSet<Review> reviewSet = this.hotelReviewHashMap.get(hotelId);
            if (reviewSet.size() > 0) {
                reviewInfoResponse.addProperty("success", true);
                reviewInfoResponse.addProperty("hotelId", hotelId);
                JsonArray reviewArray = new JsonArray();
                for (Review review : reviewSet) {
                    if (requestedCount == 0) {
                        break;
                    }
                    JsonObject reviewJson = new JsonObject();
                    reviewJson.addProperty("reviewId", review.getReviewId());
                    reviewJson.addProperty("title", review.getReviewTitle());
                    reviewJson.addProperty("user", review.getUserNickName());
                    reviewJson.addProperty("reviewText", review.getReviewText());
                    reviewJson.addProperty("date", review.getReviewSubmissionDate().toString());
                    reviewArray.add(reviewJson);
                    requestedCount--;
                }
                reviewInfoResponse.add("reviews", reviewArray);
            }
        } else {
            reviewInfoResponse.addProperty("success", false);
            reviewInfoResponse.addProperty("hotelId", "invalid");
        }
        return reviewInfoResponse;
    }

    /**
     * Returns hotel and weather details in Json format
     *
     * @param hotelId String
     * @return JsonObject
     */
    public JsonObject getWeatherDataInJsonFormat(String hotelId) {
        JsonObject weatherInfoResponse = new JsonObject();
        if (this.hotelDetailsHashMap.containsKey(hotelId)) {
            Hotel hotel = this.hotelDetailsHashMap.get(hotelId);
            if (hotel != null) {
                Gson gson = new Gson();
                WeatherDataFetcher weatherDataFetcher = new WeatherDataFetcher();
                JsonObject weatherDetails = weatherDataFetcher.consumeWeatherApi(hotel.getHotelCoordinates().getHotelLatitude(), hotel.getHotelCoordinates().getHotelLongitude());
                weatherInfoResponse.addProperty("success", true);
                weatherInfoResponse.addProperty("hotelId", hotel.getHotelId());
                weatherInfoResponse.addProperty("name", hotel.getHotelName());
                weatherInfoResponse.addProperty("addr", hotel.getHotelAddress());
                weatherInfoResponse.addProperty("city", hotel.getHotelCity());
                weatherInfoResponse.addProperty("state", hotel.getHotelState());
                weatherInfoResponse.addProperty("temperature", weatherDetails.get("temperature").getAsBigDecimal());
                weatherInfoResponse.addProperty("windspeed", weatherDetails.get("windspeed").getAsBigDecimal());
                weatherInfoResponse.addProperty("winddirection", weatherDetails.get("winddirection").getAsBigDecimal());
                weatherInfoResponse.addProperty("weathercode", weatherDetails.get("weathercode").getAsInt());
            } else {
                weatherInfoResponse.addProperty("success", false);
                weatherInfoResponse.addProperty("hotelId", "invalid");
            }
        } else {
            weatherInfoResponse.addProperty("success", false);
            weatherInfoResponse.addProperty("hotelId", "invalid");
        }
        return weatherInfoResponse;
    }

    /**
     * Returns set of reviews containing a specific word in Json Format
     *
     * @param word String
     * @param requestedCount int
     * @return JsonObject
     */
    public JsonObject getReviewsContainingSpecificWordInJsonFormat(String word, int requestedCount) {
        JsonObject wordResponse = new JsonObject();
        if (this.wordHashMap.containsKey(word) && !word.equals("") && requestedCount != 0) {
            SortedSet<Review> reviewSet = this.wordHashMap.get(word);
            if (reviewSet.size() > 0) {
                wordResponse.addProperty("success", true);
                wordResponse.addProperty("word", word);
                JsonArray reviewArray = new JsonArray();
                for (Review review : reviewSet) {
                    if (requestedCount == 0) {
                        break;
                    }
                    JsonObject reviewJson = new JsonObject();
                    reviewJson.addProperty("reviewId", review.getReviewId());
                    reviewJson.addProperty("title", review.getReviewTitle());
                    reviewJson.addProperty("user", review.getUserNickName());
                    reviewJson.addProperty("reviewText", review.getReviewText());
                    reviewJson.addProperty("date", review.getReviewSubmissionDate().toString());
                    reviewArray.add(reviewJson);
                    requestedCount--;
                }
                wordResponse.add("reviews", reviewArray);
            } else {
                wordResponse.addProperty("success", false);
                wordResponse.addProperty("word", "invalid");
            }
        } else {
            wordResponse.addProperty("success", false);
            wordResponse.addProperty("word", "invalid");
        }
        return wordResponse;
    }

    /**
     * Creates hashmap of HotelId and Hotels to search the details with efficiency
     *
     * @param hotels List of Hotel objects
     */
    public void updateHotelHashMap(List<Hotel> hotels) {
        try {
            for (Hotel hotel : hotels) {
                if (!hotelDetailsHashMap.containsKey(hotel.getHotelId())) {
                    hotelDetailsHashMap.put(hotel.getHotelId(), hotel);
                }
            }
        } catch (Exception ex) {
            LogHelper.getLogger().error(ex);
        }
    }

    /**
     * Creates a Hashmap of HotelId and Reviews to search reviews of certain hotel efficiently.
     *
     * @param reviews List of Review objects
     */
    public void updateHotelReviewHashMap(List<Review> reviews) {
        try {
            ReviewByDateComparator reviewByDateComparator = new ReviewByDateComparator();
            for (Review review : reviews) {
                if (hotelReviewHashMap.containsKey(review.getHotelId())) {
                    SortedSet<Review> reviewTreeSet = hotelReviewHashMap.get(review.getHotelId());
                    reviewTreeSet.add(review);
                    reviewIdSet.add(review.getReviewId());
                    hotelReviewHashMap.put(review.getHotelId(), reviewTreeSet);
                } else {
                    TreeSet<Review> reviewTreeSet = new TreeSet<>(reviewByDateComparator);
                    reviewTreeSet.add(review);
                    hotelReviewHashMap.put(review.getHotelId(), reviewTreeSet);
                }
            }
        } catch (Exception ex) {
            LogHelper.getLogger().error(ex);
        }

    }

    /**
     * Creates a Hashmap of Words and Reviews containing those words to search by words efficiently.
     *
     * @param reviews List of Review objects
     */
    public void updateWordHashMap(List<Review> reviews) {
        try {
            StopWords stopWords = new StopWords();
            Set<String> stopWordHashSet = stopWords.populateStopWordList();

            for (Review review : reviews) {
                Map<String, Integer> reviewWordFrequency = review.getWordFrequency();
                for (String word : reviewWordFrequency.keySet()) {
                    if (!stopWordHashSet.contains(word) && !word.equals("")) {

                        if (wordHashMap.containsKey(word)) {
                            SortedSet<Review> sortedWordSet = wordHashMap.get(word);
                            sortedWordSet.add(review);
                            wordHashMap.put(word, sortedWordSet);
                        } else {
                            ReviewByWordOccurrenceComparator reviewByWordOccurrenceComparator = new ReviewByWordOccurrenceComparator(word);
                            TreeSet<Review> sortedWordSet = new TreeSet<>(reviewByWordOccurrenceComparator);
                            sortedWordSet.add(review);
                            wordHashMap.put(word, sortedWordSet);
                        }
                    }
                }
            }
        } catch (Exception ex) {
            LogHelper.getLogger().error(ex);
        }
    }

    /**
     * Updates the review hashmap with edited review object
     *
     * @param updatedReview
     */

    public void updateHotelReview(Review updatedReview) {
        try {
            ReviewByDateComparator reviewByDateComparator = new ReviewByDateComparator();
            if (hotelReviewHashMap.containsKey(updatedReview.getHotelId())) {
                SortedSet<Review> reviewTreeSet = hotelReviewHashMap.get(updatedReview.getHotelId());
                Review oldReview=null;
                for (Review review: reviewTreeSet){
                    if(review.getReviewId().equals(updatedReview.getReviewId())){
                        oldReview = review;
                    }
                }
                if (oldReview != null) {
                    reviewTreeSet.remove(oldReview);
                    reviewTreeSet.add(updatedReview);
                }
                hotelReviewHashMap.put(updatedReview.getHotelId(), reviewTreeSet);
            }
        } catch (Exception ex) {
            LogHelper.getLogger().error(ex);
        }
    }

    /**
     * Deletes the review from Hotel reviews hashmap
     * @param deleteReview
     */
    public void deleteHotelReview(Review deleteReview) {
        try {
            ReviewByDateComparator reviewByDateComparator = new ReviewByDateComparator();
            if (hotelReviewHashMap.containsKey(deleteReview.getHotelId())) {
                SortedSet<Review> reviewTreeSet = hotelReviewHashMap.get(deleteReview.getHotelId());
                Review oldReview=null;
                for (Review review: reviewTreeSet){
                    if(review.getReviewId().equals(deleteReview.getReviewId())){
                        oldReview = review;
                    }
                }
                if (oldReview != null) {
                    reviewTreeSet.remove(oldReview);
                }
                hotelReviewHashMap.put(deleteReview.getHotelId(), reviewTreeSet);
            }
        } catch (Exception ex) {
            LogHelper.getLogger().error(ex);
        }
    }
}
