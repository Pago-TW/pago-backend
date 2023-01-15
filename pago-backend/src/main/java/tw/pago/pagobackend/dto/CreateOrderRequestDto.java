package tw.pago.pagobackend.dto;

import com.fasterxml.jackson.annotation.JsonEnumDefaultValue;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.util.Date;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.format.annotation.DateTimeFormat;
import tw.pago.pagobackend.constant.CurrencyEnum;
import tw.pago.pagobackend.constant.OrderStatusEnum;
import tw.pago.pagobackend.constant.PackagingEnum;

public class CreateOrderRequestDto {

  @NotNull
  @JsonProperty(value = "orderItem")
  private CreateOrderItemDto createOrderItemDto;

  private String orderId;

  @NotNull
  private PackagingEnum packaging;

  @NotNull
  private String verification;

  @NotNull
  private String destination;

  @NotNull
  private BigDecimal travelerFee;

  @NotNull
  private CurrencyEnum currency;

  @Value("4.5")
  private Double platformFeePercent;

  @Value("2.5")
  private Double tariffFeePercent;

  @NotNull
  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
  private Date latestReceiveItemDate;

  private String note;

  @Value("REQUESTED")
  private OrderStatusEnum orderStatus;


  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public CreateOrderItemDto getCreateOrderItemDto() {
    return createOrderItemDto;
  }

  public void setCreateOrderItemDto(CreateOrderItemDto createOrderItemDto) {
    this.createOrderItemDto = createOrderItemDto;
  }

  public PackagingEnum getPackaging() {
    return packaging;
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
}
