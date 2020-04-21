package pt.ua.icm.treesteps;

public class TravelPoint {
    private double latitude;
    private double longitude;
    private String travelType;

    public TravelPoint() {
        this.latitude = 0.0;
        this.longitude = 0.0;
        this.travelType = "";
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getTravelType() {
        return travelType;
    }

    public void setTravelType(String travelType) {
        this.travelType = travelType;
    }

    @Override
    public String toString() {
        return "TravelPoint{" +
                "latitude=" + latitude +
                ", longitude=" + longitude +
                ", travelType='" + travelType + '\'' +
                '}';
    }
}
