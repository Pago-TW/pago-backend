package tw.pago.pagobackend.rowmapper;

import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import tw.pago.pagobackend.model.PhoneVerification;

public class PhoneVerificationRowMapper implements RowMapper<PhoneVerification> {
        
        @Override
        public PhoneVerification mapRow(java.sql.ResultSet rs, int rowNum) throws SQLException {
            return PhoneVerification.builder()
                .verificationId(rs.getString("verification_id"))
                .userId(rs.getString("user_id"))
                .phone(rs.getString("phone"))
                .isPhoneVerified(rs.getBoolean("is_phone_verified"))
                .createDate(rs.getTimestamp("create_date").toLocalDateTime())
                .build();
        }
    
}
