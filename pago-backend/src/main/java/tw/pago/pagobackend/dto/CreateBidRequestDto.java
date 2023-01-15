package tw.pago.pagobackend.dto;

import java.math.BigDecimal;
import java.util.Date;
import javax.validation.constraints.NotNull;
import tw.pago.pagobackend.constant.CurrencyEnum;

public class CreateBidRequestDto {

  private String bidId;
  private Integer orderId;
  private Integer tripId;

  @NotNull
  private BigDecimal bidAmount;

  private CurrencyEnum currency;


  public String getBidId() {
    return bidId;
  }

  public void setBidId(String bidId) {
    this.bidId = bidId;
  }

  public Integer getOrderId() {
    return orderId;
  }

  public void setOrderId(Integer orderId) {
    this.orderId = orderId;
  }

  public Integer getTripId() {
    return tripId;
  }

  public void setTripId(Integer tripId) {
    this.tripId = tripId;
  }

  public BigDecimal getBidAmount() {
    return bidAmount;
  }

  public void setBidAmount(BigDecimal bidAmount) {
    this.bidAmount = bidAmount;
  }

  public CurrencyEnum getCurrency() {
    return currency;
  }

  public void setCurrency(CurrencyEnum currency) {
    this.currency = currency;
  }
}
