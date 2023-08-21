package hotelapp;

import java.util.Set;

/**
 * This class has methods to efficiently search hotel details, reviews of a hotel and reviews containing a given word.
 * By: Gandhar Kulkarni
 */
public class EfficientSearch {
    private ThreadSafeHotelDataBuilder hashMapBuilder;

    public EfficientSearch(ThreadSafeHotelDataBuilder hashMapBuilder) {
        this.hashMapBuilder = hashMapBuilder;
    }

    /**
     * Finds hotel details for respective hotel id
     *
     * @param hotelId hotel id of a hotel
     */
    public void findHotel(String hotelId) {
        try {
            Hotel hotelDetails = hashMapBuilder.getHotelByHotelId(hotelId);
            if (hotelDetails != null) {
                System.out.println(hotelDetails);
            } else {
                System.out.println("Invalid Hotel Id. No records found.");
            }
        } catch (Exception ex) {
            LogHelper.getLogger().error(ex);
        }
    }

    /**
     * Finds reviews of the given hotelId
     *
     * @param hotelId HotelId
     */
    public void findReviews(String hotelId) {
        try {
            Set<Review> reviewSortedSet = hashMapBuilder.getAllReviewsForHotel(hotelId);
            if (reviewSortedSet != null) {
                System.out.println(" Review count: " + reviewSortedSet.size());
                for (Review review : reviewSortedSet) {
                    System.out.println(review);
                }
                System.out.println("Total reviews for hotel id " + hotelId + " = " + reviewSortedSet.size());
            } else {
                System.out.println("Invalid Hotel Id. No records found.");
            }
        } catch (Exception ex) {
            LogHelper.getLogger().error(ex);
        }
    }

    /**
     * Finds reviews containing given word
     *
     * @param word Word
     */
    public void findWord(String word) {
        try {
            Set<Review> reviewSortedSet = hashMapBuilder.getReviewsContainingSpecificWord(word.toLowerCase());
            if (reviewSortedSet != null) {
                for (Review review : reviewSortedSet) {
                    System.out.println(review);
                    System.out.println(" Frequency: " + review.getWordFrequency().get(word));
                }
                System.out.println(" Total Matching Reviews : " + reviewSortedSet.size());
            } else {
                System.out.println("No records found for word " + word);
            }
        } catch (Exception ex) {
            LogHelper.getLogger().error(ex);
        }
    }
}
