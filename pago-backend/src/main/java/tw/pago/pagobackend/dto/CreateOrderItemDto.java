package tw.pago.pagobackend.dto;

import java.math.BigDecimal;
import javax.validation.constraints.NotNull;

public class CreateOrderItemDto {

  @NotNull
  private String orderItemId;

  @NotNull
  private String name;

  private String imageUrl;
  private String description;

  @NotNull
  private Integer quantity;

  @NotNull
  private BigDecimal unitPrice;

  @NotNull
  private String purchaseLocation;

  public String getOrderItemId() {
    return orderItemId;
  }

  public void setOrderItemId(String orderItemId) {
    this.orderItemId = orderItemId;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getImageUrl() {
    return imageUrl;
  }

  public void setImageUrl(String imageUrl) {
    this.imageUrl = imageUrl;
  }

  public String getDescription() {
    return description;
  }

  public void setDescription(String description) {
    this.description = description;
  }

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public BigDecimal getUnitPrice() {
    return unitPrice;
  }

  public void setUnitPrice(BigDecimal unitPrice) {
    this.unitPrice = unitPrice;
  }

  public String getPurchaseLocation() {
    return purchaseLocation;
  }

  public void setPurchaseLocation(String purchaseLocation) {
    this.purchaseLocation = purchaseLocation;
  }
}
