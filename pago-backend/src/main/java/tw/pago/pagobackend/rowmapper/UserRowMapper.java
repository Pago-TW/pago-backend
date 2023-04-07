package tw.pago.pagobackend.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import tw.pago.pagobackend.constant.UserAuthProviderEnum;
import tw.pago.pagobackend.model.User;

public class UserRowMapper implements RowMapper<User> {

  @Override
  public User mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    return User.builder()
        .userId(resultSet.getString("user_id"))
        .account(resultSet.getString("account"))
        .password(resultSet.getString("password"))
        .firstName(resultSet.getString("first_name"))
        .lastName(resultSet.getString("last_name"))
        .phone(resultSet.getString("phone"))
        .email(resultSet.getString("email"))
        .gender(resultSet.getString("gender"))
        .googleId(resultSet.getString("google_id"))
        .accountStatus(resultSet.getString("account_status"))
        .updateDate(resultSet.getTimestamp("update_date"))
        .createDate(resultSet.getTimestamp("create_date"))
        .avatarUrl(resultSet.getString("avatar_url"))
        .aboutMe(resultSet.getString("about_me"))
        .country(resultSet.getString("country"))
        .lastLogin(resultSet.getTimestamp("last_login"))
        .provider(UserAuthProviderEnum.valueOf(resultSet.getString("provider")))
        .build();
  }
}
