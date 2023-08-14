package tw.pago.pagobackend.dto;

import java.math.BigDecimal;
import java.util.Date;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tw.pago.pagobackend.constant.BidStatusEnum;
import tw.pago.pagobackend.constant.CurrencyEnum;


@Data
@NoArgsConstructor
public class BidResponseDto {

  private String bidId;
  private String orderId;
  private BidCreatorDto creator;
  private BidTripDto trip;
  private BigDecimal bidAmount;
  private CurrencyEnum currency;
  private Date createDate;
  private Date updateDate;
  private Date latestDeliveryDate;
  private BidStatusEnum bidStatus;
  private String bidComment;



  // Json Example
//  {
//    "bidId": "79609d822cb5428f977f9392750fb490",
//      "creator":{
//    "userId":"5a195bf4c2e941cfb0fc297f9c48ad96",
//        "name":"SHIUN CHIU",
//        "avatarUrl":"https://lh3.googleusercontent.com/a/AGNmyxYCn5ZTzVOK_r0TIN829tKCiI1zxd7e84okgIpI_TA=s96-c",
//        "review":{
//      "averageRating":4.5,
//          "totalReivew":16,
//          "reviewType":"FOR_SHOPPER"
//    }
//
//  },
//    "orderId": "6b6c319911ff4b85bdabdba7da0eda3f",
//      "trip": {
//    "tripId":"b0745d65d4944027966496bac3d339d4",
//        "fromCountry": "Taiwan",
//        "fromCity":"Taipei",
//        "toCountry":"Japan",
//        "toCity": "Tokyo",
//        "arrivalDate": "2022-12-25 23:37:50"
//  },
//    "bidAmount": 30,
//      "currency": "USD",
//      "createDate": "2023-03-11T16:05:19.000+00:00",
//      "updateDate": "2023-03-11T16:05:19.000+00:00",
//      "bidStatus": "NOT_CHOSEN"
//  }


}
