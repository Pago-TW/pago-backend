package tw.pago.pagobackend.dto;


import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class BidTripDto {
  private String tripId;
  private String fromCountry;
  private String fromCity;
  private String toCountry;
  private String toCity;
  private Date arrivalDate;

}
