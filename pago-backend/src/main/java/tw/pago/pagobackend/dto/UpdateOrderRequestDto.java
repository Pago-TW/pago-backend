// package tw.pago.pagobackend.dto;

// import com.fasterxml.jackson.annotation.JsonIgnore;
// import java.math.BigDecimal;
// import java.util.Date;
// import tw.pago.pagobackend.constant.CurrencyEnum;
// import tw.pago.pagobackend.constant.OrderStatusEnum;
// import tw.pago.pagobackend.constant.PackagingEnum;

// public class UpdateOrderRequestDto {

//   private String orderId;

//   @JsonIgnore
//   private String orderItemId;

//   private Integer consumerId;
//   private Date createDate;
//   private Date updateDate;
//   private PackagingEnum packaging;
//   private String verification;
//   private String destination;
//   private BigDecimal travelerFee;
//   private CurrencyEnum currency;
//   private Double platformFeePercent;
//   private Double tariffFeePercent;
//   private Date latestReceiveItemDate;
//   private String note;
//   private OrderStatusEnum orderStatus;

//   public String getOrderId() {
//     return orderId;
//   }

//   public void setOrderId(String orderId) {
//     this.orderId = orderId;
//   }

//   public String getOrderItemId() {
//     return orderItemId;
//   }

//   public void setOrderItemId(String orderItemId) {
//     this.orderItemId = orderItemId;
//   }

//   public Integer getConsumerId() {
//     return consumerId;
//   }

//   public void setConsumerId(Integer consumerId) {
//     this.consumerId = consumerId;
//   }

//   public Date getCreateDate() {
//     return createDate;
//   }

//   public void setCreateDate(Date createDate) {
//     this.createDate = createDate;
//   }

//   public Date getUpdateDate() {
//     return updateDate;
//   }

//   public void setUpdateDate(Date updateDate) {
//     this.updateDate = updateDate;
//   }

//   public PackagingEnum getPackaging() {
//     return packaging;
//   }

//   public void setPackaging(PackagingEnum packaging) {
//     this.packaging = packaging;
//   }

//   public String getVerification() {
//     return verification;
//   }

//   public void setVerification(String verification) {
//     this.verification = verification;
//   }

//   public String getDestination() {
//     return destination;
//   }

//   public void setDestination(String destination) {
//     this.destination = destination;
//   }

//   public BigDecimal getTravelerFee() {
//     return travelerFee;
//   }

//   public void setTravelerFee(BigDecimal travelerFee) {
//     this.travelerFee = travelerFee;
//   }

//   public CurrencyEnum getCurrency() {
//     return currency;
//   }

//   public void setCurrency(CurrencyEnum currency) {
//     this.currency = currency;
//   }

//   public Double getPlatformFeePercent() {
//     return platformFeePercent;
//   }

//   public void setPlatformFeePercent(Double platformFeePercent) {
//     this.platformFeePercent = platformFeePercent;
//   }

//   public Double getTariffFeePercent() {
//     return tariffFeePercent;
//   }

//   public void setTariffFeePercent(Double tariffFeePercent) {
//     this.tariffFeePercent = tariffFeePercent;
//   }

//   public Date getLatestReceiveItemDate() {
//     return latestReceiveItemDate;
//   }

//   public void setLatestReceiveItemDate(Date latestReceiveItemDate) {
//     this.latestReceiveItemDate = latestReceiveItemDate;
//   }

//   public String getNote() {
//     return note;
//   }

//   public void setNote(String note) {
//     this.note = note;
//   }

//   public OrderStatusEnum getOrderStatus() {
//     return orderStatus;
//   }

//   public void setOrderStatus(OrderStatusEnum orderStatus) {
//     this.orderStatus = orderStatus;
//   }
// }
