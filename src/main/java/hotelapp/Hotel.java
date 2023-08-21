package hotelapp;

import com.google.gson.annotations.SerializedName;

/**
 * This class stores the details for various hotels.
 * By: Gandhar Kulkarni
 */
public class Hotel {
    @SerializedName( value = "id")
    private final String hotelId;
    @SerializedName(value = "f")
    private final String hotelName;
    @SerializedName(value = "ad")
    private final String hotelAddress;
    @SerializedName(value = "ll")
    private final HotelCoordinates hotelCoordinates;
    @SerializedName(value = "ci")
    private final String hotelCity;
    @SerializedName(value = "pr")
    private final String hotelState;
    @SerializedName(value = "c")
    private final String country;
    public Hotel(String id, String name, String address, HotelCoordinates coordinates, String hotelCity, String hotelState, String country){
        this.hotelId = id;
        this.hotelName = name;
        this.hotelAddress = address;
        this.hotelCoordinates = coordinates;
        this.hotelCity = hotelCity;
        this.hotelState = hotelState;
        this.country = country;
    }

    /**
     * Returns Hotel ID
     * @return String hotelId
     */
    public String getHotelId() {
        return hotelId;
    }

    /**
     * Returns Hotel Name
     * @return String hotelName
     */
    public String getHotelName() {
        return hotelName;
    }

    /**
     * Returns Hotel Address
     * @return String hotelAddress
     */
    public String getHotelAddress() {
        return hotelAddress;
    }

    /**
     * Returns Hotel Coordinates
     * @return HotelCoordinates hotelCoordinates
     */
    public HotelCoordinates getHotelCoordinates() {
        return hotelCoordinates;
    }

    /**
     * Returns Hotel City
     * @return String hotelCity
     */
    public String getHotelCity() {
        return hotelCity;
    }

    /**
     * Returns Hotel State
     * @return String hotelState
     */
    public String getHotelState() {
        return hotelState;
    }

    /**
     * Returns Hotel Country
     * @return String country
     */
    public String getCountry() {
        return country;
    }

    /**
     * Prints details of hotel in readable format
     * @return String hotelDetails
     */
    @Override
    public String toString() {
        return "\n Hotel Id: " +this.getHotelId()+
                "\n Hotel Name: " + this.getHotelName()+
                "\n Hotel Address: " + this.getHotelAddress()+", "+ this.getHotelCity()+", "+ this.getHotelState()+", "+ this.getCountry()+
                "\n Hotel Latitude Coordinates: "+ this.getHotelCoordinates().getHotelLatitude()+
                "\n Hotel Longitude Coordinates: "+ this.getHotelCoordinates().getHotelLongitude()+
                "\n -------------------------------------------------------------";
    }
}
