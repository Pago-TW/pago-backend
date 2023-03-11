package tw.pago.pagobackend.model;

import java.math.BigDecimal;
import java.util.Date;

import tw.pago.pagobackend.constant.BidStatusEnum;
import tw.pago.pagobackend.constant.CurrencyEnum;

public class Bid {
  private String bidId;
  private String orderId;
  private String tripId;
  private BigDecimal bidAmount;
  private CurrencyEnum currency;
  private Date createDate;
  private Date updateDate;
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

  public BidStatusEnum getBidStatus() {
    return bidStatus;
  }

  public void setBidStatus(BidStatusEnum bidStatus) {
    this.bidStatus = bidStatus;
  }
}
