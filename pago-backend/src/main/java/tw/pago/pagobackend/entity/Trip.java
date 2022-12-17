package tw.pago.pagobackend.entity;

public class Trip {
    private Integer trip_id;
    private Integer traveler_id;
    private String from_location;
    private String to_location;
    private String arrival_date;
    private double profit;

    public Trip(Integer trip_id, Integer traveler_id, String from_location, String to_location, String arrival_date, double profit) {
        this.trip_id = trip_id;
        this.traveler_id = traveler_id;
        this.from_location = from_location;
        this.to_location = to_location;
        this.arrival_date = arrival_date;
        this.profit = profit;
    }

    public Integer getTripId() {
        return trip_id;
    }

    public void setTripId(Integer trip_id) {
        this.trip_id = trip_id;
    }

    public Integer getTravelerId() {
        return traveler_id;
    }

    public void setTravelerId(Integer traveler_id) {
        this.traveler_id = traveler_id;
    }

    public String getFromLocation() {
        return from_location;
    }

    public void setFromLocation(String from_location) {
        this.from_location = from_location;
    }

    public String getToLocation() {
        return to_location;
    }

    public void setToLocation(String to_location) {
        this.to_location = to_location;
    }

    public String getArrivalDate() {
        return arrival_date;
    }

    public void setArrivalDate(String arrival_date) {
        this.arrival_date = arrival_date;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }
}
