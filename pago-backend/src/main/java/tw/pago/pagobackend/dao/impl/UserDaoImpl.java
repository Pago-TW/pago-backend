package tw.pago.pagobackend.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Component;
import tw.pago.pagobackend.dao.UserDao;
import tw.pago.pagobackend.dto.UserRegisterRequestDto;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.rowmapper.UserRowMapper;

@Component
public class UserDaoImpl implements UserDao {

  @Autowired
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Override
  public Integer createUser(UserRegisterRequestDto userRegisterRequestDto) {
    String sql = "INSERT INTO user(account, password, first_name, last_name, "
        + "phone, email, gender, google_id, account_status, update_date, "
        + "create_date, avatar_url, about_me, country, last_login) "
        + "VALUES (:account, :password, :firstName, :lastName, :phone, "
        + ":email, :gender, :googleId, :accountStatus, :updateDate, "
        + ":createDate, :avatarUrl, :aboutMe, :country, :lastLogin)";

    Map<String, Object> map = new HashMap<>();
    map.put("account", userRegisterRequestDto.getAccount());
    map.put("password", userRegisterRequestDto.getPassword());
    map.put("firstName", userRegisterRequestDto.getFirstName());
    map.put("lastName", userRegisterRequestDto.getLastName());
    map.put("phone", userRegisterRequestDto.getPhone());
    map.put("email", userRegisterRequestDto.getEmail());
    map.put("gender", userRegisterRequestDto.getGender());
    map.put("googleId", userRegisterRequestDto.getGoogleId());
    map.put("accountStatus", userRegisterRequestDto.getAccountStatus());

    Date now = new Date();
    map.put("updateDate", now);
    map.put("createDate", now);
    map.put("avatarUrl", userRegisterRequestDto.getAvatarUrl());
    map.put("aboutMe", userRegisterRequestDto.getAboutMe());
    map.put("country", userRegisterRequestDto.getCountry());
    map.put("lastLogin", now);

    KeyHolder keyHolder = new GeneratedKeyHolder();

    namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map), keyHolder);

    int userId = keyHolder.getKey().intValue();

    return userId;
  }

  @Override
  public User getUserById(Integer userId) {
    String sql = "SELECT user_id, account, password, first_name, last_name,"
        + "phone, email, gender, google_id, account_status, update_date,"
        + "create_date, avatar_url, about_me, country, last_login "
        + "FROM user "
        + "WHERE user_id = :userId ";

    Map<String, Object> map = new HashMap<>();
    map.put("userId", userId);

    List<User> userList = namedParameterJdbcTemplate.query(sql, map, new UserRowMapper());

    if (userList.size() > 0) {
      return userList.get(0);
    } else {
      return null;
    }
  }

  @Override
  public User getUserByEmail(String email) {
    String sql = "SELECT user_id, account, password, first_name, last_name,"
        + "phone, email, gender, google_id, account_status, update_date,"
        + "create_date, avatar_url, about_me, country, last_login "
        + "FROM user "
        + "WHERE email = :email ";

    Map<String, Object> map = new HashMap<>();
    map.put("email", email);

    List<User> userList = namedParameterJdbcTemplate.query(sql, map, new UserRowMapper());

    if (userList.size() > 0) {
      return userList.get(0);
    } else {
      return null;
    }
  }
}
