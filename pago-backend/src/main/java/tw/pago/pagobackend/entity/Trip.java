package tw.pago.pagobackend.entity;

public class Trip {
    private Integer tripId;
    private Integer travelerId;
    private String fromLocation;
    private String toLocation;
    private String arrivalDate;
    private double profit;

    public Trip(Integer tripId, Integer travelerId, String fromLocation, String toLocation, String arrivalDate, double profit) {
        this.tripId = tripId;
        this.travelerId = travelerId;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.arrivalDate = arrivalDate;
        this.profit = profit;
    }

    public Integer getTripId() {
        return tripId;
    }

    public void setTripId(Integer tripId) {
        this.tripId = tripId;
    }

    public Integer getTravelerId() {
        return travelerId;
    }

    public void setTravelerId(Integer travelerId) {
        this.travelerId = travelerId;
    }

    public String getFromLocation() {
        return fromLocation;
    }

    public void setFromLocation(String fromLocation) {
        this.fromLocation = fromLocation;
    }

    public String getToLocation() {
        return toLocation;
    }

    public void setToLocation(String toLocation) {
        this.toLocation = toLocation;
    }

    public String getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(String arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }
}
