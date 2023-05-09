package tw.pago.pagobackend.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import tw.pago.pagobackend.model.PasswordResetToken;

public class PasswordResetTokenRowMapper implements RowMapper<PasswordResetToken>{
    @Override
    public PasswordResetToken mapRow(ResultSet rs, int rowNum) throws SQLException {
        return PasswordResetToken.builder()
            .passwordResetTokenId(rs.getString("password_reset_token_id"))
            .userId(rs.getString("user_id"))
            .token(rs.getString("token"))
            .expiryDate(rs.getTimestamp("expiry_date").toLocalDateTime())
            .createDate(rs.getTimestamp("create_date").toLocalDateTime())
            .build();
    }
}
