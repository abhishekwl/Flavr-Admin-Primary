package io.github.abhishekwl.flavradminprimary.Models;

public class Hotel {

    private String hotelName;
    private String hotelImageUrl;
    private String hotelEmailId;
    private String hotelContactNumber;
    private double hotelLatitude, hotelLongitude, hotelAltitude;
    private int hotelRange;

    public Hotel(String hotelName, String hotelEmailId, String hotelImageUrl, String hotelContactNumber, double hotelLatitude, double hotelLongitude, double hotelAltitude, int hotelRange) {
        this.hotelName = hotelName;
        this.hotelImageUrl = hotelImageUrl;
        this.hotelEmailId = hotelEmailId;
        this.hotelContactNumber = hotelContactNumber;
        this.hotelLatitude = hotelLatitude;
        this.hotelLongitude = hotelLongitude;
        this.hotelAltitude = hotelAltitude;
        this.hotelRange = hotelRange;
    }

    public Hotel(String hotelName, String hotelEmailId, String hotelContactNumber, double hotelLatitude, double hotelLongitude, double hotelAltitude) {
        this.hotelName = hotelName;
        this.hotelImageUrl = "";
        this.hotelEmailId = hotelEmailId;
        this.hotelContactNumber = hotelContactNumber;
        this.hotelLatitude = hotelLatitude;
        this.hotelLongitude = hotelLongitude;
        this.hotelAltitude = hotelAltitude;
        this.hotelRange = 50;
    }

    public Hotel(String hotelName, String hotelEmailId, String hotelImageUrl, String hotelContactNumber, double hotelLatitude, double hotelLongitude, double hotelAltitude) {
        this.hotelName = hotelName;
        this.hotelImageUrl = hotelImageUrl;
        this.hotelEmailId = hotelEmailId;
        this.hotelContactNumber = hotelContactNumber;
        this.hotelLatitude = hotelLatitude;
        this.hotelLongitude = hotelLongitude;
        this.hotelAltitude = hotelAltitude;
        this.hotelRange = 50;
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

    public double getHotelLatitude() {
        return hotelLatitude;
    }

    public void setHotelLatitude(double hotelLatitude) {
        this.hotelLatitude = hotelLatitude;
    }

    public double getHotelLongitude() {
        return hotelLongitude;
    }

    public void setHotelLongitude(double hotelLongitude) {
        this.hotelLongitude = hotelLongitude;
    }

    public int getHotelRange() {
        return hotelRange;
    }

    public void setHotelRange(int hotelRange) {
        this.hotelRange = hotelRange;
    }

    public double getHotelAltitude() {
        return hotelAltitude;
    }

    public void setHotelAltitude(double hotelAltitude) {
        this.hotelAltitude = hotelAltitude;
    }
}
