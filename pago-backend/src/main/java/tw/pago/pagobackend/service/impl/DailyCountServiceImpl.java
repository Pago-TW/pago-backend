package tw.pago.pagobackend.service.impl;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tw.pago.pagobackend.dao.DailyCountDao;
import tw.pago.pagobackend.dto.CreateDailyCountRequestDto;
import tw.pago.pagobackend.model.DailyCount;
import tw.pago.pagobackend.service.DailyCountService;
import tw.pago.pagobackend.util.CurrentUserInfoProvider;
import tw.pago.pagobackend.util.UuidGenerator;

@Service
@AllArgsConstructor
public class DailyCountServiceImpl implements DailyCountService {

  private final DailyCountDao dailyCountDao;
  private final CurrentUserInfoProvider currentUserInfoProvider;
  private final UuidGenerator uuidGenerator;

  @Override
  public void incrementSmsCount(String userId) {
    ZonedDateTime today = ZonedDateTime.now(ZoneId.of("UTC"));
    String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();
    DailyCount dailyCount = dailyCountDao.getDailyCountByUserId(userId);
    if (dailyCount == null) {
      CreateDailyCountRequestDto createDailyCountRequestDto = new CreateDailyCountRequestDto();
      createDailyCountRequestDto.setUserId(currentLoginUserId);
      createDailyCountRequestDto.setSmsCount(1);
      createDailyCountRequestDto.setEmailCount(0);

      createDailyCount(createDailyCountRequestDto);

    }

  }

  @Override
  public void incrementEmailCount(String userId) {

  }

  @Override
  public void createDailyCount(CreateDailyCountRequestDto createDailyCountRequestDto) {
    String dailyCountId = uuidGenerator.getUuid();

    createDailyCountRequestDto.setDailyCountId(dailyCountId);

    dailyCountDao.createDailyCount(createDailyCountRequestDto);
  }
}
