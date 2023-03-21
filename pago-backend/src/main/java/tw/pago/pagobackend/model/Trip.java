package tw.pago.pagobackend.model;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class Trip {
    private String tripId;
    private String shopperId;
    private String fromCountry;
    private String fromCity;
    private String toCountry;
    private String toCity;
    private Date arrivalDate;
    private BigDecimal profit;
    private Date createDate;
    private Date updateDate;

}
