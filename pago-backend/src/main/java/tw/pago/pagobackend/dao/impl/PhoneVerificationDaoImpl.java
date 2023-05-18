package tw.pago.pagobackend.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import tw.pago.pagobackend.dao.PhoneVerificationDao;
import tw.pago.pagobackend.dto.PhoneVerificationDto;
import tw.pago.pagobackend.model.PhoneVerification;
import tw.pago.pagobackend.rowmapper.PhoneVerificationRowMapper;

@Component
@AllArgsConstructor
public class PhoneVerificationDaoImpl implements PhoneVerificationDao {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Override
    public void createPhoneVerification(PhoneVerificationDto phoneVerificationDto) {
        String sql = "INSERT INTO phone_verification (phone_verification_id, user_id, phone, is_phone_verified, create_date) " +
        "VALUES (:verificationId, :userId, :phone, :isPhoneVerified, :createDate)";

        Map<String, Object> map = new HashMap<>();
        map.put("verificationId", phoneVerificationDto.getVerificationId());
        map.put("userId", phoneVerificationDto.getUserId());
        map.put("phone", phoneVerificationDto.getPhone());
        map.put("isPhoneVerified", phoneVerificationDto.isPhoneVerified());
        map.put("createDate", phoneVerificationDto.getCreateDate());

        namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map));
        
    }

    @Override
    public PhoneVerification getPhoneVerificationByUserId(String userId) {
        String sql = "SELECT phone_verification_id, user_id, phone, is_phone_verified, create_date " +
        "FROM phone_verification WHERE user_id = :userId";

        Map<String, Object> map = new HashMap<>();
        map.put("userId", userId);

        List<PhoneVerification> verificationList = namedParameterJdbcTemplate.query(sql, map, new PhoneVerificationRowMapper());

        if (verificationList.size() > 0) {
            return verificationList.get(0);
        } else {
            return null;
        }
    }
}
