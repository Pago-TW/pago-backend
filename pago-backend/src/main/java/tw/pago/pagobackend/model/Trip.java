package tw.pago.pagobackend.model;

import java.math.BigDecimal;
import java.util.Date;

public class Trip {
    private Integer tripId;
    private Integer travelerId;
    private String fromLocation;
    private String toLocation;
    private Date arrivalDate;
    private BigDecimal profit;
    private Date createDate;
    private Date updateDate;

    public Trip(Integer tripId, Integer travelerId, String fromLocation, String toLocation, Date arrivalDate, BigDecimal profit) {
        this.tripId = tripId;
        this.travelerId = travelerId;
        this.fromLocation = fromLocation;
        this.toLocation = toLocation;
        this.arrivalDate = arrivalDate;
        this.profit = profit;
    }

    public Trip() {

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

    public Date getArrivalDate() {
        return arrivalDate;
    }

    public void setArrivalDate(Date arrivalDate) {
        this.arrivalDate = arrivalDate;
    }

    public BigDecimal getProfit() {
        return profit;
    }

    public void setProfit(BigDecimal profit) {
        this.profit = profit;
    }

    public Date getCreateDate() {
        return createDate;
    }

    public void setCreateDate(Date createDate) {
        this.createDate = createDate;
    }

    public Date getUpdateDate() {
        return updateDate;
    }

    public void setUpdateDate(Date updateDate) {
        this.updateDate = updateDate;
    }
}
