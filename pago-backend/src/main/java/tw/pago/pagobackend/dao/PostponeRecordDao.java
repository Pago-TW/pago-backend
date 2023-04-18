package tw.pago.pagobackend.dao;

import tw.pago.pagobackend.dto.CreatePostponeRecordRequestDto;
import tw.pago.pagobackend.dto.UpdatePostponeRecordRequestDto;
import tw.pago.pagobackend.model.PostponeRecord;

public interface PostponeRecordDao {

  void createPostponeRecord(CreatePostponeRecordRequestDto createPostponeRecordRequestDto);

  PostponeRecord getPostponeRecordById(String postponeRecordId);

  PostponeRecord getPostponeRecordByOrderId(String orderId);

  void updatePostponeRecord(UpdatePostponeRecordRequestDto updatePostponeRecordRequestDto);
}
