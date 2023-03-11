package tw.pago.pagobackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import tw.pago.pagobackend.constant.CurrencyEnum;
import tw.pago.pagobackend.constant.OrderStatusEnum;
// import tw.pago.pagobackend.constant.PackagingEnum;

public class UpdateOrderAndOrderItemRequestDto {


  private String orderId;

  private String consumerId;

  @JsonProperty(value = "orderItem")
  private Optional<UpdateOrderItemDto> updateOrderItemDto;


  private boolean packaging;


  private boolean verification;


  private String destination;


  private BigDecimal travelerFee;


  private CurrencyEnum currency;

  @Value("4.5")
  private Double platformFeePercent;

  @Value("2.5")
  private Double tariffFeePercent;


  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
  private Date latestReceiveItemDate;

  private String note;

  private OrderStatusEnum orderStatus;


  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public String getConsumerId() {
    return consumerId;
  }

  public void setConsumerId(String consumerId) {
    this.consumerId = consumerId;
  }

  public Optional<UpdateOrderItemDto> getUpdateOrderItemDto() {
    return updateOrderItemDto;
  }

  public void setUpdateOrderItemDto(Optional<UpdateOrderItemDto> updateOrderItemDto) {
    this.updateOrderItemDto = updateOrderItemDto;
  }

  public boolean getPackaging() {
    return packaging;
  }

  public void setPackaging(boolean packaging) {
    this.packaging = packaging;
  }

  public boolean getVerification() {
    return verification;
  }

  public void setVerification(boolean verification) {
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
}
