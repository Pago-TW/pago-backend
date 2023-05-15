package tw.pago.pagobackend.dao.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.AllArgsConstructor;
import tw.pago.pagobackend.dao.AuthDao;
import tw.pago.pagobackend.model.PasswordResetToken;
import tw.pago.pagobackend.rowmapper.PasswordResetTokenRowMapper;

@Repository
@AllArgsConstructor
public class AuthDaoImpl implements AuthDao{
    
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void createToken(PasswordResetToken passwordResetToken) {
        String sql = "INSERT INTO password_reset_token (password_reset_token_id, user_id, token, expiry_date, create_date) "
            + "VALUES (:passwordResetTokenId, :userId, :token, :expiryDate, :createDate)";

        Map<String, Object> map = new HashMap<>();
        LocalDateTime now = LocalDateTime.now();
        map.put("passwordResetTokenId", passwordResetToken.getPasswordResetTokenId());
        map.put("userId", passwordResetToken.getUserId());
        map.put("token", passwordResetToken.getToken());
        map.put("expiryDate", passwordResetToken.getExpiryDate());
        map.put("createDate", now);

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map));
    }

    @Override
    public PasswordResetToken getPasswordResetTokenByToken(String token) {
        String sql = "SELECT password_reset_token_id, user_id, token, expiry_date, create_date " +
        "FROM password_reset_token WHERE token = :token";

        Map<String, Object> map = new HashMap<>();
        map.put("token", token);

        List<PasswordResetToken> passwordResetTokenList = namedParameterJdbcTemplate.query(sql, map, new PasswordResetTokenRowMapper());

        if (passwordResetTokenList.size() > 0) {
            return passwordResetTokenList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public PasswordResetToken getPasswordResetTokenByUserId(String userId) {
        String sql = "SELECT password_reset_token_id, user_id, token, expiry_date, create_date " +
        "FROM password_reset_token WHERE user_id = :userId";

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);

        List<PasswordResetToken> passwordResetTokenList = namedParameterJdbcTemplate.query(sql, map, new PasswordResetTokenRowMapper());

        if (passwordResetTokenList.size() > 0) {
            return passwordResetTokenList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public void deletePasswordResetTokenById(String tokenId) {
        String sql = "DELETE FROM password_reset_token WHERE password_reset_token_id = :tokenId";

        Map<String, Object> map = new HashMap<>();
        map.put("tokenId", tokenId);

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map));
    }
}
