package tw.pago.pagobackend.service;

import tw.pago.pagobackend.dto.CreateDailyCountRequestDto;

public interface DailyCountService {

  void incrementSmsCount(String userId);

  void incrementEmailCount(String userId);

  void createDailyCount(CreateDailyCountRequestDto createDailyCountRequestDto);

}
