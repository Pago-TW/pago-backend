package tw.pago.pagobackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
import java.util.Date;
import tw.pago.pagobackend.constant.CurrencyEnum;
import tw.pago.pagobackend.constant.OrderStatusEnum;
import tw.pago.pagobackend.constant.PackagingEnum;

public class Order {

  // Order
  private Integer orderId;

  @JsonIgnore
  private Integer orderItemId;

  private Integer shopperId;
  private Date createDate;
  private Date updateDate;
  private PackagingEnum packaging;
  private String verification;
  private String destination;
  private BigDecimal travelerFee;
  private CurrencyEnum currency;
  private Double platformFeePercent;
  private Double tariffFeePercent;
  private Date latestReceiveItemDate;
  private String note;
  private OrderStatusEnum orderStatus;

  // OrderItem
  private OrderItem orderItem;
//  private String name;
//  private String imageUrl;
//  private String description;
//  private Integer quantity;
//  private BigDecimal unitPrice;
//  private String purchaseLocation;

  // Order
  public Integer getOrderId() {
    return orderId;
  }

  public void setOrderId(Integer orderId) {
    this.orderId = orderId;
  }

  public Integer getOrderItemId() {
    return orderItemId;
  }

  public void setOrderItemId(Integer orderItemId) {
    this.orderItemId = orderItemId;
  }

  public Integer getShopperId() {
    return shopperId;
  }

  public void setShopperId(Integer shopperId) {
    this.shopperId = shopperId;
  }

  public Date getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }

  public PackagingEnum getPackaging() {
    return packaging;
  }

  public Date getUpdateDate() {
    return updateDate;
  }

  public void setUpdateDate(Date updateDate) {
    this.updateDate = updateDate;
  }

  public void setPackaging(PackagingEnum packaging) {
    this.packaging = packaging;
  }

  public String getVerification() {
    return verification;
  }

  public void setVerification(String verification) {
    this.verification = verification;
  }

  public String getDestination() {
    return destination;
  }

  public void setDestination(String destination) {
    this.destination = destination;
  }

  public BigDecimal getTravelerFee() {
    return travelerFee;
  }

  public void setTravelerFee(BigDecimal travelerFee) {
    this.travelerFee = travelerFee;
  }

  public CurrencyEnum getCurrency() {
    return currency;
  }

  public void setCurrency(CurrencyEnum currency) {
    this.currency = currency;
  }

  public Double getPlatformFeePercent() {
    return platformFeePercent;
  }

  public void setPlatformFeePercent(Double platformFeePercent) {
    this.platformFeePercent = platformFeePercent;
  }

  public Double getTariffFeePercent() {
    return tariffFeePercent;
  }

  public void setTariffFeePercent(Double tariffFeePercent) {
    this.tariffFeePercent = tariffFeePercent;
  }

  public Date getLatestReceiveItemDate() {
    return latestReceiveItemDate;
  }

  public void setLatestReceiveItemDate(Date latestReceiveItemDate) {
    this.latestReceiveItemDate = latestReceiveItemDate;
  }

  public String getNote() {
    return note;
  }

  public void setNote(String note) {
    this.note = note;
  }

  public OrderStatusEnum getOrderStatus() {
    return orderStatus;
  }

  public void setOrderStatus(OrderStatusEnum orderStatus) {
    this.orderStatus = orderStatus;
  }



  // OrderItem

  public OrderItem getOrderItem() {
    return orderItem;
  }

  public void setOrderItem(OrderItem orderItem) {
    this.orderItem = orderItem;
  }


}
