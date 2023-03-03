package tw.pago.pagobackend.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.util.Date;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;

public class CreateTripRequestDto {

  @NotNull
  private String travelerId;

  @NotBlank
  private String fromLocation;

  @NotBlank
  private String toLocation;

  @NotNull
  @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss")
  private Date arrivalDate;


  public String getTravelerId() {
    return travelerId;
  }

  public void setTravelerId(String travelerId) {
    this.travelerId = travelerId;
  }

  public String getFromLocation() {
    return fromLocation;
  }

  public void setFromLocation(String fromLocation) {
    this.fromLocation = fromLocation;
  }

  public String getToLocation() {
    return toLocation;
  }

  public void setToLocation(String toLocation) {
    this.toLocation = toLocation;
  }

  public Date getArrivalDate() {
    return arrivalDate;
  }

  public void setArrivalDate(Date arrivalDate) {
    this.arrivalDate = arrivalDate;
  }
}
