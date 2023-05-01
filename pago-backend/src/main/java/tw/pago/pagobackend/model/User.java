package tw.pago.pagobackend.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.Date;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
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
  private String gender; // TODO 改用 ENUM 可能就 男、女、不公開
  private String googleId;
  private String accountStatus; // TODO 改用 ENUM，這邊原本是想說可能會有檢舉的功能，還有官方後台的功能，可以去看 Dropbox 產品規格的UserStory 5.官方服務，可能就會有印象這是我們很早期討論到的功能，但目前我們專題狀況應該是用不到，正常的帳號都會是"啟用狀態"，所以剛創好的帳號預設就會是"啟用狀態"，然後可能它被檢舉過就改成"警告狀態"上線可能就提醒他注意行為舉止之類的，再被瘋狂檢舉就送它"停權狀態"之類的....
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
