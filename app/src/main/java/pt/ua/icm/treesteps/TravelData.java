package pt.ua.icm.treesteps;

public class TravelData {
    private String date;
    private String travelType;
    private int distance;

    public TravelData() {
        this.date = "";
        this.travelType = "walk";
        this.distance = 0;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getTravelType() {
        return travelType;
    }

    public void setTravelType(String travelType) {
        this.travelType = travelType;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

    @Override
    public String toString() {
        return "TravelData{" +
                "date='" + date + '\'' +
                ", travelType='" + travelType + '\'' +
                ", distance=" + distance +
                '}';
    }
}
