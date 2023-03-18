package tw.pago.pagobackend.rowmapper;


import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import tw.pago.pagobackend.constant.UserProviderEnum;
import tw.pago.pagobackend.model.User;

public class UserRowMapper implements RowMapper<User> {

  @Override
  public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    User user = new User();
    user.setUserId(resultSet.getString("user_id"));
    user.setAccount(resultSet.getString("account"));
    user.setPassword(resultSet.getString("password"));
    user.setFirstName(resultSet.getString("first_name"));
    user.setLastName(resultSet.getString("last_name"));
    user.setPhone(resultSet.getString("phone"));
    user.setEmail(resultSet.getString("email"));
    user.setGender(resultSet.getString("gender"));
    user.setGoogleId(resultSet.getString("google_id"));
    user.setAccountStatus(resultSet.getString("account_status"));
    user.setUpdateDate(resultSet.getTimestamp("update_date"));
    user.setCreateDate(resultSet.getTimestamp("create_date"));
    user.setAvatarUrl(resultSet.getString("avatar_url"));
    user.setAboutMe(resultSet.getString("about_me"));
    user.setCountry(resultSet.getString("country"));
    user.setLastLogin(resultSet.getTimestamp("last_login"));
    user.setProvider(UserProviderEnum.valueOf(resultSet.getString("provider")));


    return user;
  }
}
