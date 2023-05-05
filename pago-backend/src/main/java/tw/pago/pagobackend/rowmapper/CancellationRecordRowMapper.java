package tw.pago.pagobackend.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import tw.pago.pagobackend.constant.CancelReasonCategoryEnum;
import tw.pago.pagobackend.model.CancellationRecord;

public class CancellationRecordRowMapper implements RowMapper<CancellationRecord> {

  @Override
  public CancellationRecord mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    CancellationRecord cancellationRecord = new CancellationRecord();
    cancellationRecord.setCancellationRecordId(resultSet.getString("cancellation_record_id"));
    cancellationRecord.setOrderId(resultSet.getString("order_id"));
    cancellationRecord.setUserId(resultSet.getString("user_id"));
    cancellationRecord.setCancelReason(
        CancelReasonCategoryEnum.valueOf(resultSet.getString("cancel_reason")));
    cancellationRecord.setNote(resultSet.getString("note"));
    cancellationRecord.setCreateDate(resultSet.getTimestamp("create_date").toLocalDateTime());
    cancellationRecord.setUpdateDate(resultSet.getTimestamp("update_date").toLocalDateTime());
    cancellationRecord.setIsCancelled(resultSet.getBoolean("is_cancelled"));

    return cancellationRecord;
  }
}
