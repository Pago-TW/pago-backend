package tw.pago.pagobackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.util.Date;

public class UpdateTripRequestDto {


  private String tripId;

  private String shopperId;

  private String fromCountry;

  private String toCountry;

  private String fromCity;

  private String toCity;

  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
  private Date arrivalDate;

  private BigDecimal profit;
  private Date createDate;
  private Date updateDate;


  public String getTripId() {
    return tripId;
  }

  public void setTripId(String tripId) {
    this.tripId = tripId;
  }

  public String getShopperId() {
    return shopperId;
  }

  public void setShopperId(String shopperId) {
    this.shopperId = shopperId;
  }

  public String getFromCountry() {
    return fromCountry;
  }

  public void setFromCountry(String fromCountry) {
    this.fromCountry = fromCountry;
  }

  public String getToCountry() {
    return toCountry;
  }

  public void setToCountry(String toCountry) {
    this.toCountry = toCountry;
  }

  public String getFromCity() {
    return fromCity;
  }

  public void setFromCity(String fromCity) {
    this.fromCity = fromCity;
  }

  public String getToCity() {
    return toCity;
  }

  public void setToCity(String toCity) {
    this.toCity = toCity;
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