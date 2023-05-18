package tw.pago.pagobackend.dao.impl;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import tw.pago.pagobackend.dao.OtpDao;
import tw.pago.pagobackend.model.Otp;
import tw.pago.pagobackend.rowmapper.OtpRowMapper;

@Component
@AllArgsConstructor
public class OtpDaoImpl implements OtpDao {
    
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void createOtp(Otp otp) {
        String sql = "INSERT INTO otp (otp_id, international_phone_number, otp_code, expiry_date, create_date) VALUES " +
        "(:otpId, :international_phone_number, :otp_code, :expiryDate, :createDate)";

        Map<String, Object> map = new HashMap<>();
        map.put("otpId", otp.getOtpId());
        map.put("international_phone_number", otp.getInternationalPhoneNumber());
        map.put("otp_code", otp.getOtpCode());
        map.put("expiryDate", Timestamp.from(otp.getExpiryDate().toInstant()));
        map.put("createDate", Timestamp.from(otp.getCreateDate().toInstant()));


        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map));
    }

    @Override
    public Otp getOtpByOtpCode(String otpCode) {
        String sql = "SELECT otp_id, international_phone_number, otp_code, expiry_date, create_date " +
        "FROM otp WHERE otp_code = :otpCode";

        Map<String, Object> map = new HashMap<>();
        map.put("otpCode", otpCode);

        List<Otp> otpList = namedParameterJdbcTemplate.query(sql, map, new OtpRowMapper());

        if (otpList.size() > 0) {
            return otpList.get(0);
        } else {
            return null;
        }   
    }

    @Override
    public Otp getOtpByPhone(String phone) {
        String sql = "SELECT otp_id, international_phone_number, otp_code, expiry_date, create_date " +
        "FROM otp WHERE international_phone_number = :phone";

        Map<String, Object> map = new HashMap<>();
        map.put("phone", phone);

        List<Otp> otpList = namedParameterJdbcTemplate.query(sql, map, new OtpRowMapper());

        if (otpList.size() > 0) {
            return otpList.get(0);
        } else {
            return null;
        }
    }

    @Override
    public void deleteOtpById(String otpId) {
        String sql = "DELETE FROM otp WHERE otp_id = :otpId";

        Map<String, Object> map = new HashMap<>();
        map.put("otpId", otpId);

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map));
    }
}
