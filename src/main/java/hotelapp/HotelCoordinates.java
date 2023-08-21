package hotelapp;

import com.google.gson.annotations.SerializedName;

/**
 * This class stores the Geo Coordinates of the hotel.
 * By: Gandhar Kulkarni
 */
public class HotelCoordinates {
    @SerializedName(value = "lat")
    private final double hotelLatitude;
    @SerializedName(value = "lng")
    private final double hotelLongitude;

    public HotelCoordinates(double latitude, double longitude){

        this.hotelLatitude = latitude;
        this.hotelLongitude = longitude;
    }

    /**
     * Returns latitude value of hotel
     * @return double Latitude value of Coordinates
     */
    public double getHotelLatitude() {
        return hotelLatitude;
    }

    /**
     * Returns longitude value of hotel
     * @return double Longitude value of Coordinates
     */
    public double getHotelLongitude() {
        return hotelLongitude;
    }
}
