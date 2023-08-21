package hotelapp;

import java.util.Comparator;

/**
 * This class implements Comparator to sort reviews based on the date of submission
 * By: Gandhar Kulkarni
 */
public class ReviewByDateComparator implements Comparator<Review> {
    /**
     * Compares reviews based on date of submission.
     * @param reviewOne the first object to be compared.
     * @param reviewTwo the second object to be compared.
     * @return int
     */
    @Override
    public int compare(Review reviewOne, Review reviewTwo){
        if(reviewOne.getReviewSubmissionDate().isEqual(reviewTwo.getReviewSubmissionDate())){
            return reviewOne.getReviewId().compareTo(reviewTwo.getReviewId());
        }
        else{
            if(reviewOne.getReviewSubmissionDate().isAfter(reviewTwo.getReviewSubmissionDate())){
                return -1;
            }
            else{
                return 1;
            }
        }
    }
}
