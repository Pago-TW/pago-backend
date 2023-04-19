package tw.pago.pagobackend.dao.impl;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import tw.pago.pagobackend.dao.PaymentDao;
import tw.pago.pagobackend.dto.CreatePaymentRequestDto;
import tw.pago.pagobackend.dto.UpdatePaymentRequestDto;
import tw.pago.pagobackend.model.Payment;

@Repository
@AllArgsConstructor
public class PaymentDaoImpl implements PaymentDao {

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;


  @Override
  public void createPayment(CreatePaymentRequestDto createPaymentRequestDto) {
    String sql = "INSERT INTO payment (payment_id, order_id, is_paid, create_date, update_date) "
        + "VALUES (:paymentId, :orderId, :isPaid, :createDate, :updateDate)";

    Map<String, Object> map = new HashMap<>();

    LocalDate now = LocalDate.now();
    map.put("paymentId", createPaymentRequestDto.getPaymentId());
    map.put("orderId", createPaymentRequestDto.getOrderId());
    map.put("isPaid", createPaymentRequestDto.getIsPaid());
    map.put("createDate", now);
    map.put("updateDate", now);

    namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map));
  }

  @Override
  public Payment getPaymentById(String paymentId) {


    return null;
  }

  @Override
  public void updatePayment(UpdatePaymentRequestDto updatePaymentRequestDto) {

  }
}
