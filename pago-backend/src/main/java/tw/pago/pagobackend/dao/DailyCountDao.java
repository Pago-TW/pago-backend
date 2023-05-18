package tw.pago.pagobackend.dao;

import tw.pago.pagobackend.dto.CreateDailyCountRequestDto;
import tw.pago.pagobackend.model.DailyCount;

public interface DailyCountDao {

  void createDailyCount(CreateDailyCountRequestDto createDailyCountRequestDto);

  DailyCount getDailyCountByUserId(String userId);

  void updateSmsCount(String userId);

  void updateEmailCount(String userId);

}
