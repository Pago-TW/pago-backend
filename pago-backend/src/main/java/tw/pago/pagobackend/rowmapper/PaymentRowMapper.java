package tw.pago.pagobackend.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import tw.pago.pagobackend.model.Payment;

public class PaymentRowMapper implements RowMapper<Payment> {


  @Override
  public Payment mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    Payment payment = new Payment();
    payment.setPaymentId(resultSet.getString("payment_id"));
    payment.setOrderId(resultSet.getString("order_id"));
    payment.setIsPaid(resultSet.getBoolean("is_paid"));
    payment.setCreateDate(resultSet.getTimestamp("create_date").toLocalDateTime());
    payment.setUpdateDate(resultSet.getTimestamp("update_date").toLocalDateTime());


    return payment;
  }
}
