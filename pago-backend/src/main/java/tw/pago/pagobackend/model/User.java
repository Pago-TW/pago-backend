package tw.pago.pagobackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import tw.pago.pagobackend.constant.AccountStatusEnum;
import tw.pago.pagobackend.constant.GenderEnum;
import tw.pago.pagobackend.constant.UserAuthProviderEnum;

@Getter
@Setter
@Builder
public class User {
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
  private Date updateDate;
  private Date createDate;
  private String aboutMe;
  private String country;
  private Date lastLogin;
  private String avatarUrl;
  // private Integer persent;

  // For programing use;
  private String fullName;



  // For Spring Security use
  private UserAuthProviderEnum provider; // GOOGLE, LOCAL
  private String role;
  private boolean enabled;

  public String getFullName() {
    return firstName + " " + lastName;
  }
}
