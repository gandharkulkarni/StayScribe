package hotelapp;
import com.google.gson.annotations.SerializedName;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * This class stores review details of various hotels.
 * By: Gandhar Kulkarni
 */
public class Review {
    private final String hotelId;
    private final String reviewId;
    @SerializedName(value = "ratingOverall")
    private final double overallRating;
    @SerializedName(value = "title")
    private final String reviewTitle;
    private final String reviewText;
    @SerializedName(value = "userNickname")
    private final String userNickName;
    private final LocalDate reviewSubmissionDate;

    private Map<String, Integer> wordFrequency;

    public Review(String hotelId, String reviewId, double overallRating, String reviewTitle, String reviewText, String userNickName, String reviewSubmissionDate) {
        this.wordFrequency = new HashMap<>();
        this.hotelId = hotelId;
        this.reviewId = reviewId;
        this.overallRating = overallRating;
        this.reviewTitle = reviewTitle;
        this.reviewText = reviewText;
        if(userNickName.equals("")){
            this.userNickName = "Anonymous";
        }
        else {
            this.userNickName = userNickName;
        }
        this.reviewSubmissionDate = LocalDate.parse(reviewSubmissionDate, DateTimeFormatter.ISO_DATE_TIME);
        countWordFrequency(reviewText);
    }

    /**
     * Counts the frequency of each word in the review text and stores in hashmap for future reference.
     * @param reviewText String
     */
    private void countWordFrequency(String reviewText){
        reviewText = reviewText.replaceAll("[^A-Za-z ]", " ");
        String[] reviewWords = reviewText.split(" ");

        for (String word : reviewWords) {
            word = word.toLowerCase();
            if(!word.equals("")){
                if(!this.wordFrequency.containsKey(word)){
                    this.wordFrequency.put(word,1);
                }
                else{
                    this.wordFrequency.put(word,this.wordFrequency.get(word)+1);
                }
            }
        }
    }

    /**
     * Returns Hotel ID
     * @return String HotelId
     */
    public String getHotelId() {
        return hotelId;
    }

    /**
     * Returns Review ID
     * @return String ReviewId
     */
    public String getReviewId() {
        return reviewId;
    }

    /**
     * Returns average rating of hotel
     * @return double overallRating
     */
    public double getOverallRating() {
        return overallRating;
    }

    /**
     * Returns review title
     * @return String reviewTitle
     */
    public String getReviewTitle() {
        return reviewTitle;
    }

    /**
     * Returns review text
     * @return String reviewText
     */
    public String getReviewText() {
        return reviewText;
    }

    /**
     * Returns nickname of user who posted the review
     * @return String userNickName
     */
    public String getUserNickName() {
        return userNickName;
    }

    /**
     * Returns review submission date
     * @return LocalDateTime reviewSubmissionDate
     */
    public LocalDate getReviewSubmissionDate() {
        return reviewSubmissionDate;
    }

    /**
     * Returns HashMap containing word and respective frequency
     * @return Map<String,Integer>
     */
    public Map<String,Integer> getWordFrequency(){
        return Collections.unmodifiableMap(this.wordFrequency);
    }

    /**
     * Prints details of review in readable format
     * @return String reviewDetails
     */
    @Override
    public String toString() {
        return "\n Hotel Id: " + this.getHotelId() +
                "\n Review Id: " + this.getReviewId() +
                "\n Average rating: "+ this.getOverallRating()+
                "\n Title: "+ this.getReviewTitle()+
                "\n Review : " + this.getReviewText()+
                "\n Submission Date : "+ this.getReviewSubmissionDate()+
                "\n From: "+ this.getUserNickName()
                + "\n -------------------------------------------------------------";
    }
}
