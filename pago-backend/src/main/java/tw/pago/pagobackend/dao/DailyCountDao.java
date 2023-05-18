package tw.pago.pagobackend.dao;

import java.time.ZonedDateTime;
import tw.pago.pagobackend.dto.CreateDailyCountRequestDto;
import tw.pago.pagobackend.model.DailyCount;

public interface DailyCountDao {

  void createDailyCount(CreateDailyCountRequestDto createDailyCountRequestDto);

  DailyCount getDailyCountByUserId(String userId);

  DailyCount getDailyCountByUserIdAndCreateDate(String userId, ZonedDateTime createDate);

  void updateSmsCount(String userId);

  void updateEmailCount(String userId);

}
