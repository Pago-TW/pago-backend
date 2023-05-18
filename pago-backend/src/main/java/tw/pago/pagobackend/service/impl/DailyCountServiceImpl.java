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

  private static final Integer MAX_DAILY_SMS = 10;
  private static final Integer MAX_DAILY_EMAIL = 50;

  private final DailyCountDao dailyCountDao;
  private final UuidGenerator uuidGenerator;

  @Override
  public void incrementSmsCount(String userId) {
    // Get the current date and time in UTC
    ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
    // Extract the date part and convert it back to ZonedDateTime at the start of the day
    ZonedDateTime today = now.toLocalDate().atStartOfDay(ZoneId.of("UTC"));

    // Retrieve the DailyCount for the given user and today's date
    DailyCount dailyCount = dailyCountDao.getDailyCountByUserIdAndCreateDate(userId, today);
    if (dailyCount == null) {
      // If no DailyCount exists, create a new one with SMS count of 1
      CreateDailyCountRequestDto createDailyCountRequestDto = new CreateDailyCountRequestDto();
      createDailyCountRequestDto.setUserId(userId);
      createDailyCountRequestDto.setSmsCount(1);
      createDailyCountRequestDto.setEmailCount(0);

      createDailyCount(createDailyCountRequestDto);

    } else {
      // If DailyCount already exists, increment the SMS count
      dailyCountDao.incrementTodaySmsCount(userId, today);
    }
  }

  @Override
  public void incrementEmailCount(String userId) {

    ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
    ZonedDateTime today = now.toLocalDate().atStartOfDay(ZoneId.of("UTC"));

    DailyCount dailyCount = dailyCountDao.getDailyCountByUserIdAndCreateDate(userId, today);
    if (dailyCount == null) {
      CreateDailyCountRequestDto createDailyCountRequestDto = new CreateDailyCountRequestDto();
      createDailyCountRequestDto.setUserId(userId);
      createDailyCountRequestDto.setSmsCount(0);
      createDailyCountRequestDto.setEmailCount(1);

      createDailyCount(createDailyCountRequestDto);

    } else {
      dailyCountDao.incrementTodayEmailCount(userId, today);
    }

  }

  @Override
  public void createDailyCount(CreateDailyCountRequestDto createDailyCountRequestDto) {
    String dailyCountId = uuidGenerator.getUuid();

    createDailyCountRequestDto.setDailyCountId(dailyCountId);

    dailyCountDao.createDailyCount(createDailyCountRequestDto);
  }

  @Override
  public boolean isReachedDailySmsLimit(String userId) {
    ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
    ZonedDateTime today = now.toLocalDate().atStartOfDay(ZoneId.of("UTC"));

    DailyCount dailyCount = dailyCountDao.getDailyCountByUserIdAndCreateDate(userId, today);
    if (dailyCount == null) {
      return false;
    }

    return dailyCount.getSmsCount() >= MAX_DAILY_SMS;
  }

  @Override
  public boolean isReachedDailyEmailLimit(String userId) {
    ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC"));
    ZonedDateTime today = now.toLocalDate().atStartOfDay(ZoneId.of("UTC"));

    DailyCount dailyCount = dailyCountDao.getDailyCountByUserIdAndCreateDate(userId, today);
    if (dailyCount == null) {
      return false;
    }

    return dailyCount.getEmailCount() >= MAX_DAILY_EMAIL;
  }
}
