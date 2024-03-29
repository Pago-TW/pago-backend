package tw.pago.pagobackend.dto;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import java.util.Date;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import tw.pago.pagobackend.constant.AccountStatusEnum;
import tw.pago.pagobackend.constant.GenderEnum;
import tw.pago.pagobackend.constant.UserAuthProviderEnum;

@Data
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "userId",
    "email",
    "account",
    "firstName",
    "lastName",
    "fullName",
    "phone",
    "provider",
    "role",
    "enabled",
    "createDate",
    "updateDate",
    "lastLogin"})
public class UserDto {
  private String userId;
  private String account;

  @JsonIgnore
  private String password;

  private String firstName;
  private String lastName;
  private String phone;
  private String email;
  private GenderEnum gender;
  private String googleId;
  private AccountStatusEnum accountStatus;
  private String aboutMe;
  private String country;
  private Date lastLogin;
  private String avatarUrl;
  private Date updateDate;
  private Date createDate;
  // private Integer persent;

  // For programing use;
  private String fullName;
  private Boolean isPhoneVerified;



  // For Spring Security use
  private UserAuthProviderEnum provider; // GOOGLE, LOCAL
  private String role;
  private boolean enabled;

  public String getFullName() {
    return firstName + " " + lastName;
  }
}
