package tw.pago.pagobackend.dto;

import java.math.BigDecimal;
import tw.pago.pagobackend.constant.BidStatusEnum;
import tw.pago.pagobackend.constant.CurrencyEnum;

public class UpdateBidRequestDto {

  private String bidId;

  private String orderId;
  
  private String tripId;

  private BigDecimal bidAmount;

  private CurrencyEnum currency;

  private BidStatusEnum bidStatus;


  public String getBidId() {
    return bidId;
  }

  public void setBidId(String bidId) {
    this.bidId = bidId;
  }

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public String getTripId() {
    return tripId;
  }

  public void setTripId(String tripId) {
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

  public BidStatusEnum getBidStatus() {
    return bidStatus;
  }

  public void setBidStatus(BidStatusEnum bidStatus) {
    this.bidStatus = bidStatus;
  }
}
