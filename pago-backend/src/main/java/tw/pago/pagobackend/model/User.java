package tw.pago.pagobackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import java.security.Provider;
import java.util.Date;
import lombok.Getter;
import lombok.Setter;
import tw.pago.pagobackend.constant.UserProviderEnum;

@Getter
@Setter
public class User {
  private String userId;
  private String account;

  @JsonIgnore
  private String password;

  private String firstName;
  private String lastName;
  private String phone;
  private String email;
  private String gender;
  private String googleId;
  private String accountStatus;
  private Date updateDate;
  private Date createDate;
  private String aboutMe;
  private String country;
  private Date lastLogin;
  // private Integer persent;

  // For Spring Security use
  private UserProviderEnum provider; // GOOGLE, LOCAL
  private String role;
  private boolean enabled;

}
