package tw.pago.pagobackend.dao;

import tw.pago.pagobackend.dto.CreateCancellationRecordRequestDto;

public interface CancellationRecordDao {
  int countCancellationRecord(String userId);

  void createCancellationRecord(CreateCancellationRecordRequestDto createCancellationRecordRequestDto);


}
