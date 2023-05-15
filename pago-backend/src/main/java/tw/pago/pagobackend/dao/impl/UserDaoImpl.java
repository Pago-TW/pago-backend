package tw.pago.pagobackend.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import tw.pago.pagobackend.dao.UserDao;
import tw.pago.pagobackend.dto.UpdateUserRequestDto;
import tw.pago.pagobackend.dto.UserRegisterRequestDto;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.rowmapper.UserRowMapper;

@Component
public class UserDaoImpl implements UserDao {
  private static final Logger log = LoggerFactory.getLogger(UserDaoImpl.class);


  @Autowired
  private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Override
  public void createUser(UserRegisterRequestDto userRegisterRequestDto) {
    String sql = "INSERT INTO user(user_id, account, password, first_name, last_name, "
        + "phone, email, gender, google_id, account_status, update_date, "
        + "create_date, avatar_url, about_me, country, last_login, provider) "
        + "VALUES (:userId, :account, :password, :firstName, :lastName, :phone, "
        + ":email, :gender, :googleId, :accountStatus, :updateDate, "
        + ":createDate, :avatarUrl, :aboutMe, :country, :lastLogin, :provider)";

    Map<String, Object> map = new HashMap<>();
    map.put("userId", userRegisterRequestDto.getUserId());
    map.put("account", userRegisterRequestDto.getAccount());
    map.put("password", userRegisterRequestDto.getPassword());
    map.put("firstName", userRegisterRequestDto.getFirstName());
    map.put("lastName", userRegisterRequestDto.getLastName());
    map.put("phone", userRegisterRequestDto.getPhone());
    map.put("email", userRegisterRequestDto.getEmail());
    map.put("gender", userRegisterRequestDto.getGender().toString());
    map.put("googleId", userRegisterRequestDto.getGoogleId());
    map.put("accountStatus", userRegisterRequestDto.getAccountStatus().toString());

    Date now = new Date();
    map.put("updateDate", now);
    map.put("createDate", now);
    map.put("avatarUrl", userRegisterRequestDto.getAvatarUrl());
    map.put("aboutMe", userRegisterRequestDto.getAboutMe());
    map.put("country", userRegisterRequestDto.getCountry());
    map.put("lastLogin", now);
    map.put("provider", userRegisterRequestDto.getProvider().toString());

    log.debug("userRegisterRequestDto: {}", userRegisterRequestDto);

    namedParameterJdbcTemplate.update(sql, map);
  }

  @Override
  public User getUserById(String userId) {
    String sql = "SELECT user_id, account, password, first_name, last_name,"
        + "phone, email, gender, google_id, account_status, update_date,"
        + "create_date, avatar_url, about_me, country, last_login, provider "
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
        + "create_date, avatar_url, about_me, country, last_login, provider "
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

  @Override
  public User getUserByPhone(String phone) {
    String sql = "SELECT user_id, account, password, first_name, last_name,"
        + "phone, email, gender, google_id, account_status, update_date,"
        + "create_date, avatar_url, about_me, country, last_login, provider "
        + "FROM user "
        + "WHERE phone = :phone ";

    Map<String, Object> map = new HashMap<>();
    map.put("phone", phone);

    List<User> userList = namedParameterJdbcTemplate.query(sql, map, new UserRowMapper());

    if (userList.size() > 0) {
      return userList.get(0);
    } else {
      return null;
    }
  }


  @Override
  public void updateUser(UpdateUserRequestDto updateUserRequestDto) {
    String sql = "UPDATE user "
        + "SET account = :account, "
        + "    password = :password, "
        + "    first_name = :firstName, "
        + "    last_name = :lastName, "
        + "    phone = :phone, "
        + "    email = :email, "
        + "    gender = :gender, "
        + "    google_id = :googleId, "
        + "    account_status = :accountStatus, "
        + "    update_date = :updateDate, "
        + "    about_me = :aboutMe, "
        + "    country = :country, "
        + "    last_login = :lastLogin, "
        + "    avatar_url = :avatarUrl "
        + "WHERE user_id = :userId";

    Map<String, Object> map = new HashMap<>();
    map.put("account", updateUserRequestDto.getAccount());
    map.put("password", updateUserRequestDto.getPassword());
    map.put("firstName", updateUserRequestDto.getFirstName());
    map.put("lastName", updateUserRequestDto.getLastName());
    map.put("phone", updateUserRequestDto.getPhone());
    map.put("email", updateUserRequestDto.getEmail());
    map.put("gender", updateUserRequestDto.getGender().toString());
    map.put("googleId", updateUserRequestDto.getGoogleId());
    map.put("accountStatus", updateUserRequestDto.getAccountStatus().toString());
    map.put("updateDate", new Date());
    map.put("aboutMe", updateUserRequestDto.getAboutMe());
    map.put("country", updateUserRequestDto.getCountry());
    map.put("lastLogin", updateUserRequestDto.getLastLogin());
    map.put("avatarUrl", updateUserRequestDto.getAvatarUrl());
    map.put("userId", updateUserRequestDto.getUserId());

    namedParameterJdbcTemplate.update(sql, map);
  }

  @Override
  public List<User> searchUsers(String query) {
    String sql = "SELECT user_id, account, password, first_name, last_name,"
    + "phone, email, gender, google_id, account_status, update_date,"
    + "create_date, avatar_url, about_me, country, last_login, provider "
    + "FROM user "
    + "WHERE account LIKE :query OR first_name LIKE :query OR last_name LIKE :query OR country LIKE :query ";

    Map<String, Object> map = new HashMap<>();
    map.put("query", "%" + query + "%");
    
    return namedParameterJdbcTemplate.query(sql, map, new UserRowMapper());
  }
}
