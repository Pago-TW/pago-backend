package tw.pago.pagobackend.dto;

import java.math.BigDecimal;
import javax.validation.constraints.NotNull;

public class UpdateOrderItemDto {

  private Integer orderItemId;


  private String name;

  private String imageUrl;

  private String description;


  private Integer quantity;


  private BigDecimal unitPrice;


  private String purchaseLocation;


  public Integer getOrderItemId() {
    return orderItemId;
  }

  public void setOrderItemId(Integer orderItemId) {
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
