package tw.pago.pagobackend.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.constant.AccountStatusEnum;
import tw.pago.pagobackend.constant.CompletionRatingEnum;
import tw.pago.pagobackend.constant.GenderEnum;
import tw.pago.pagobackend.constant.UserAuthProviderEnum;


@Data
@NoArgsConstructor
public class UserResponseDto {
  private String userId;
  private String account;
  private String firstName;
  private String lastName;
  private String phone;
  private String email;
  private GenderEnum gender;
  private AccountStatusEnum accountStatus;
  private Date updateDate;
  private Date createDate;
  private String aboutMe;
  private String country;
  private Date lastLogin;
  private String avatarUrl;

  private UserReviewDto shopperReview;
  private UserReviewDto consumerReview;
  // private Integer persent;

  // For programing use;
  private String fullName;
  private Boolean isPhoneVerified;
  @JsonProperty("isTraveling")
  private boolean isTraveling;
  private CompletionRatingEnum completionRating;



  // For Spring Security use
  private UserAuthProviderEnum provider; // GOOGLE, LOCAL
  private boolean enabled;

  public String getFullName() {
    return firstName + " " + lastName;
  }

}
