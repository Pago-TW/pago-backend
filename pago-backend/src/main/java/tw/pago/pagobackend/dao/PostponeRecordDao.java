package tw.pago.pagobackend.dao;

import tw.pago.pagobackend.dto.CreatePostponeRecordRequestDto;

public interface PostponeRecordDao {

  void createPostponeRecord(CreatePostponeRecordRequestDto createPostponeRecordRequestDto);
}
