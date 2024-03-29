package tw.pago.pagobackend.dto;

import java.util.Date;
import javax.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import tw.pago.pagobackend.constant.AccountStatusEnum;
import tw.pago.pagobackend.constant.GenderEnum;
import tw.pago.pagobackend.constant.UserAuthProviderEnum;
import tw.pago.pagobackend.validation.ValidPhone;


@Getter
@Setter
@Builder
public class UserRegisterRequestDto {
  private String userId;
  private String account;


  private String password;

  private String firstName;
  private String lastName;

  @ValidPhone
  private String phone;

  @NotBlank
  private String email;

  private GenderEnum gender;
  private String googleId;
  private AccountStatusEnum accountStatus;
  private Date updateDate;
  private Date createDate;
  private String avatarUrl;
  private String aboutMe;
  private String country;
  private Date lastLogin;

  // For Spring Security use
  private UserAuthProviderEnum provider; // GOOGLE, LOCAL
  private String role;
  private boolean enabled;
}
