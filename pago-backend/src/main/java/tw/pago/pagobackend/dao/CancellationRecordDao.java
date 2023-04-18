package tw.pago.pagobackend.dao;

import tw.pago.pagobackend.dto.CreateCancellationRecordRequestDto;
import tw.pago.pagobackend.model.CancellationRecord;

public interface CancellationRecordDao {
  int countCancellationRecord(String userId);

  void createCancellationRecord(CreateCancellationRecordRequestDto createCancellationRecordRequestDto);

  CancellationRecord getCancellationRecordById(String cancellationRecordId);

  CancellationRecord getCancellationReocrdByOrderId(String orderId);

}
