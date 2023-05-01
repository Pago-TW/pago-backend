package tw.pago.pagobackend.dto;


import com.fasterxml.jackson.annotation.JsonIgnore;
import java.util.Date;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import tw.pago.pagobackend.constant.GenderEnum;
import tw.pago.pagobackend.model.User;

@Getter
@Setter
@Builder
public class UpdateUserRequestDto {

  private String userId;
  private String account;
  private String password;
  private String firstName;
  private String lastName;
  private String phone;
  private String email;
  private GenderEnum gender;
  private String googleId;
  private String accountStatus;
  private Date updateDate;
  private String aboutMe;
  private String country;
  private Date lastLogin;
  private String avatarUrl;


  public void fillEmptyFieldsWithOldData(User oldUser) {
    setUserId(Optional.ofNullable(getUserId()).orElse(oldUser.getUserId()));
    setAccount(Optional.ofNullable(getAccount()).orElse(oldUser.getAccount()));
    setPassword(Optional.ofNullable(getPassword()).orElse(oldUser.getPassword()));
    setFirstName(Optional.ofNullable(getFirstName()).orElse(oldUser.getFirstName()));
    setLastName(Optional.ofNullable(getLastName()).orElse(oldUser.getLastName()));
    setPhone(Optional.ofNullable(getPhone()).orElse(oldUser.getPhone()));
    setEmail(Optional.ofNullable(getEmail()).orElse(oldUser.getEmail()));
    setGender(Optional.ofNullable(getGender()).orElse(oldUser.getGender()));
    setGoogleId(Optional.ofNullable(getGoogleId()).orElse(oldUser.getGoogleId()));
    setAccountStatus(Optional.ofNullable(getAccountStatus()).orElse(oldUser.getAccountStatus()));
    setUpdateDate(Optional.ofNullable(getUpdateDate()).orElse(oldUser.getUpdateDate()));
    setAboutMe(Optional.ofNullable(getAboutMe()).orElse(oldUser.getAboutMe()));
    setCountry(Optional.ofNullable(getCountry()).orElse(oldUser.getCountry()));
    setLastLogin(Optional.ofNullable(getLastLogin()).orElse(oldUser.getLastLogin()));
    setAvatarUrl(Optional.ofNullable(getAvatarUrl()).orElse(oldUser.getAvatarUrl()));
  }

}
