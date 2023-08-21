package hotelapp;

import java.util.Comparator;

/**
 * This class implements Comparator to sort reviews by occurrence of the given word.
 * By: Gandhar Kulkarni
 */
public class ReviewByWordOccurrenceComparator implements Comparator<Review> {
    private final String wordParameter;
    public ReviewByWordOccurrenceComparator(String word){
        this.wordParameter = word.toLowerCase();
    }

    /**
     * Compares reviews based on count of occurrence.
     * @param reviewOne the first object to be compared.
     * @param reviewTwo the second object to be compared.
     * @return int
     */
    @Override
    public int compare(Review reviewOne, Review reviewTwo) {
        int reviewOneWordOccurance=0;
        if(reviewOne.getWordFrequency().containsKey(this.wordParameter)) {
            reviewOneWordOccurance = reviewOne.getWordFrequency().get(this.wordParameter);
        }
        int reviewTwoWordOccurance = 0;
        if(reviewTwo.getWordFrequency().containsKey(this.wordParameter)) {
            reviewTwoWordOccurance = reviewTwo.getWordFrequency().get(this.wordParameter);
        }

        if(reviewOneWordOccurance>reviewTwoWordOccurance){
            return -1;
        }
        else if(reviewOneWordOccurance==reviewTwoWordOccurance){
            ReviewByDateComparator reviewByDateComparator = new ReviewByDateComparator();
            return reviewByDateComparator.compare(reviewOne,reviewTwo);
        }
        else{
            return 1;
        }
    }

}
