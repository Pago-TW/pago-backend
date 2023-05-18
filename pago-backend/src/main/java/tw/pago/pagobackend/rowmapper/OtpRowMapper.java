package tw.pago.pagobackend.rowmapper;

import java.sql.SQLException;

import org.springframework.jdbc.core.RowMapper;

import tw.pago.pagobackend.model.Otp;

public class OtpRowMapper implements RowMapper<Otp> {
    
    @Override
    public Otp mapRow(java.sql.ResultSet rs, int rowNum) throws SQLException {
        return Otp.builder()
            .otpId(rs.getString("otp_id"))
            .internationalPhoneNumber(rs.getString("international_phone_number"))
            .otpCode(rs.getString("otp_code"))
            .expiryDate(rs.getTimestamp("expiry_date").toLocalDateTime())
            .createDate(rs.getTimestamp("create_date").toLocalDateTime())
            .build();
    }
}
