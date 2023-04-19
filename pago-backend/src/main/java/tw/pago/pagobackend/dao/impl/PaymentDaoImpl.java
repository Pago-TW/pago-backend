package tw.pago.pagobackend.dao.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import tw.pago.pagobackend.dao.PaymentDao;
import tw.pago.pagobackend.dto.CreatePaymentRequestDto;
import tw.pago.pagobackend.dto.UpdatePaymentRequestDto;
import tw.pago.pagobackend.model.Payment;
import tw.pago.pagobackend.model.PostponeRecord;
import tw.pago.pagobackend.rowmapper.PaymentRowMapper;
import tw.pago.pagobackend.rowmapper.PostponeRecordRowMapper;

@Repository
@AllArgsConstructor
public class PaymentDaoImpl implements PaymentDao {

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;


  @Override
  public void createPayment(CreatePaymentRequestDto createPaymentRequestDto) {
    String sql = "INSERT INTO payment (payment_id, order_id, is_paid, create_date, update_date) "
        + "VALUES (:paymentId, :orderId, :isPaid, :createDate, :updateDate)";

    Map<String, Object> map = new HashMap<>();

    LocalDateTime now = LocalDateTime.now();
    map.put("paymentId", createPaymentRequestDto.getPaymentId());
    map.put("orderId", createPaymentRequestDto.getOrderId());
    map.put("isPaid", createPaymentRequestDto.getIsPaid());
    map.put("createDate", now);
    map.put("updateDate", now);

    namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map));
  }

  @Override
  public Payment getPaymentById(String paymentId) {
    String sql = "SELECT payment_id, order_id, is_paid, create_date, update_date "
        + "FROM payment "
        + "WHERE payment_id = :paymentId ";

    Map<String, Object> map = new HashMap<>();
    map.put("paymentId", paymentId);


    List<Payment> paymentList = namedParameterJdbcTemplate.query(sql, map, new PaymentRowMapper());

    if (paymentList.size() > 0) {
      return paymentList.get(0);
    } else {
      return null;
    }
  }

  @Override
  public void updatePayment(UpdatePaymentRequestDto updatePaymentRequestDto) {
    String sql = "UPDATE payment "
        + "SET is_paid = :isPaid, "
        + "    update_date = :updateDate "
        + "WHERE payment_id = :paymentId";

    Map<String, Object> map = new HashMap<>();
    LocalDateTime now = LocalDateTime.now();
    map.put("isPaid", updatePaymentRequestDto.getIsPaid());
    map.put("updateDate", now);
    map.put("paymentId", updatePaymentRequestDto.getPaymentId());

    namedParameterJdbcTemplate.update(sql, map);

  }
}
