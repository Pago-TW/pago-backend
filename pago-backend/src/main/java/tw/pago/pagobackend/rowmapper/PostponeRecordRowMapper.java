package tw.pago.pagobackend.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import tw.pago.pagobackend.constant.CancelReasonCategoryEnum;
import tw.pago.pagobackend.constant.PostponeReasonCategoryEnum;
import tw.pago.pagobackend.model.CancellationRecord;
import tw.pago.pagobackend.model.PostponeRecord;

public class PostponeRecordRowMapper implements RowMapper<PostponeRecord> {


  @Override
  public PostponeRecord mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    PostponeRecord postponeRecord = new PostponeRecord();
    postponeRecord.setPostponeRecordId(resultSet.getString("postpone_record_id"));
    postponeRecord.setOrderId(resultSet.getString("order_id"));
    postponeRecord.setUserId(resultSet.getString("user_id"));
    postponeRecord.setPostponeReason(
        PostponeReasonCategoryEnum.valueOf(resultSet.getString("postpone_reason")));
    postponeRecord.setNote(resultSet.getString("note"));
    postponeRecord.setCreateDate(resultSet.getDate("create_date").toLocalDate());
    postponeRecord.setUpdateDate(resultSet.getDate("update_date").toLocalDate());
    postponeRecord.setIsPostponed(resultSet.getBoolean("is_postponed"));

    return postponeRecord;
  }
}
