package hotelapp;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Set;

public class OutputFileHelper {
    private String outputFilePath;
    private ThreadSafeHotelDataBuilder hashMapBuilder;

    /**
     * Output Helper class contructor
     *
     * @param outputFilePath output file path
     * @param hashMapBuilder hashMapBuilder object
     */
    public OutputFileHelper(String outputFilePath, ThreadSafeHotelDataBuilder hashMapBuilder) {
        this.outputFilePath = outputFilePath;
        this.hashMapBuilder = hashMapBuilder;
    }

    /**
     * Prints hotel details in specific format
     */
    public void printHotelDetails() {
        //Map<String, SortedSet<Review>> hotelReviewHashMap = hashMapBuilder.getHotelReviewHashMap();
        Set<String> hotelIdSet = hashMapBuilder.getAllHotelIds();

        try (FileWriter fileWriter = new FileWriter(this.outputFilePath)) {

            for (String hotelId : hotelIdSet) {
                Hotel hotel = hashMapBuilder.getHotelByHotelId(hotelId);
                String hotelDetails = getHotelDetails(hotel);
                fileWriter.write(hotelDetails);
                Set<Review> reviewSortedSet = hashMapBuilder.getAllReviewsForHotel(hotelId);

                if (reviewSortedSet!=null) {
                    for (Review review : reviewSortedSet) {
                        String reviewDetails = getReviewDetails(review);
                        fileWriter.write(reviewDetails);
                    }
                }

                fileWriter.flush();
            }
        } catch (IOException ioException) {
            LogHelper.getLogger().error(ioException);
        } catch (Exception ex) {
            LogHelper.getLogger().error(ex);
        }
    }

    /**
     * Prepares the output string for each hotel object
     *
     * @param hotelObj hotel object
     * @return String
     */
    private String getHotelDetails(Hotel hotelObj) {
        StringBuilder builder = new StringBuilder();
        builder.append(System.lineSeparator());
        builder.append("********************");
        builder.append(System.lineSeparator());
        builder.append(hotelObj.getHotelName());
        builder.append(": ");
        builder.append(hotelObj.getHotelId());
        builder.append(System.lineSeparator());
        builder.append(hotelObj.getHotelAddress());
        builder.append(System.lineSeparator());
        builder.append(hotelObj.getHotelCity());
        builder.append(", ");
        builder.append(hotelObj.getHotelState());
        builder.append(System.lineSeparator());
        return builder.toString();
    }

    /**
     * Prepares review string for each review object
     *
     * @param reviewObj review object
     * @return String
     */
    private String getReviewDetails(Review reviewObj) {
        StringBuilder builder = new StringBuilder();
        builder.append("--------------------");
        builder.append(System.lineSeparator());
        builder.append("Review by ");
        builder.append(reviewObj.getUserNickName());
        builder.append(" on ");
        builder.append(reviewObj.getReviewSubmissionDate());
        builder.append(System.lineSeparator());
        builder.append("Rating: ");
        builder.append((int) reviewObj.getOverallRating());
        builder.append(System.lineSeparator());
        builder.append("ReviewId: ");
        builder.append(reviewObj.getReviewId());
        builder.append(System.lineSeparator());
        builder.append(reviewObj.getReviewTitle());
        builder.append(System.lineSeparator());
        builder.append(reviewObj.getReviewText());
        builder.append(System.lineSeparator());
        return builder.toString();
    }
}
