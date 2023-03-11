package tw.pago.pagobackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.util.Date;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class CreateTripRequestDto {

  private String tripId;

  @NotBlank
  private String fromCountry;

  @NotBlank
  private String toCountry;

  @NotBlank
  private String fromCity;

  @NotBlank
  private String toCity;

  @NotNull
  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
  private Date arrivalDate;


  public String getTripId() {
    return tripId;
  }

  public void setTripId(String tripId) {
    this.tripId = tripId;
  }

  public String getFromCountry() {
    return fromCountry;
  }

  public void setFromCountry(String fromCountry) {
    this.fromCountry = fromCountry;
  }

  public String getFromCity() {
    return fromCity;
  }

  public void setFromCity(String fromCity) {
    this.fromCity = fromCity;
  }

  public String getToCountry() {
    return toCountry;
  }

  public void setToCountry(String toCountry) {
    this.toCountry = toCountry;
  }

  public String getToCity() {
    return toCity;
  }

  public void setToCity(String toCity) {
    this.toCity = toCity;
  }

  public Date getArrivalDate() {
    return arrivalDate;
  }

  public void setArrivalDate(Date arrivalDate) {
    this.arrivalDate = arrivalDate;
  }
}
