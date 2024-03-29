package tw.pago.pagobackend.model;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import tw.pago.pagobackend.constant.CityCode;
import tw.pago.pagobackend.constant.CountryCode;
import tw.pago.pagobackend.constant.TripStatusEnum;

@Getter
@Setter
@Builder
public class Trip {
    private String tripId;
    private String shopperId;
    private String tripCollectionId;
    private CountryCode fromCountry;
    private CityCode fromCity;
    private CountryCode toCountry;
    private CityCode toCity;
    private Date arrivalDate;
    private BigDecimal profit;
    private Date createDate;
    private Date updateDate;

}
