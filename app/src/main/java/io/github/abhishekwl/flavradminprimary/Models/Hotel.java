package io.github.abhishekwl.flavradminprimary.Models;

public class Hotel {

    private String hotelName;
    private String hotelImageUrl;
    private String hotelEmailId;
    private String hotelContactNumber;
    private int hotelLatitude, hotelLongitude;

    public Hotel(String hotelName, String hotelImageUrl, String hotelEmailId, String hotelContactNumber, int hotelLatitude, int hotelLongitude) {
        this.hotelName = hotelName;
        this.hotelImageUrl = hotelImageUrl;
        this.hotelEmailId = hotelEmailId;
        this.hotelContactNumber = hotelContactNumber;
        this.hotelLatitude = hotelLatitude;
        this.hotelLongitude = hotelLongitude;
    }

    public Hotel(String hotelName, String hotelEmailId, String hotelContactNumber, int hotelLatitude, int hotelLongitude) {
        this.hotelName = hotelName;
        this.hotelImageUrl = "";
        this.hotelEmailId = hotelEmailId;
        this.hotelContactNumber = hotelContactNumber;
        this.hotelLatitude = hotelLatitude;
        this.hotelLongitude = hotelLongitude;
    }

    public String getHotelName() {
        return hotelName;
    }

    public void setHotelName(String hotelName) {
        this.hotelName = hotelName;
    }

    public String getHotelImageUrl() {
        return hotelImageUrl;
    }

    public void setHotelImageUrl(String hotelImageUrl) {
        this.hotelImageUrl = hotelImageUrl;
    }

    public String getHotelEmailId() {
        return hotelEmailId;
    }

    public void setHotelEmailId(String hotelEmailId) {
        this.hotelEmailId = hotelEmailId;
    }

    public String getHotelContactNumber() {
        return hotelContactNumber;
    }

    public void setHotelContactNumber(String hotelContactNumber) {
        this.hotelContactNumber = hotelContactNumber;
    }

    public int getHotelLatitude() {
        return hotelLatitude;
    }

    public void setHotelLatitude(int hotelLatitude) {
        this.hotelLatitude = hotelLatitude;
    }

    public int getHotelLongitude() {
        return hotelLongitude;
    }

    public void setHotelLongitude(int hotelLongitude) {
        this.hotelLongitude = hotelLongitude;
    }
}
