package tw.pago.pagobackend.model;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Builder
public class OrderItem {

  private String orderItemId;
  private String name;
  private String description;
  private Integer quantity;
  private BigDecimal unitPrice;
  private String purchaseCountry;
  private String purchaseCity;
  private String purchaseDistrict;
  private String purchaseRoad;

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

  public String getPurchaseCountry() {
    return purchaseCountry;
  }

  public void setPurchaseCountry(String purchaseCountry) {
    this.purchaseCountry = purchaseCountry;
  }

  public String getPurchaseCity() {
    return purchaseCity;
  }

  public void setPurchaseCity(String purchaseCity) {
    this.purchaseCity = purchaseCity;
  }

  public String getPurchaseDistrict() {
    return purchaseDistrict;
  }

  public void setPurchaseDistrict(String purchaseDistrict) {
    this.purchaseDistrict = purchaseDistrict;
  }

  public String getPurchaseRoad() {
    return purchaseRoad;
  }

  public void setPurchaseRoad(String purchaseRoad) {
    this.purchaseRoad = purchaseRoad;
  }

}
