package tw.pago.pagobackend.service;

public interface DailyCountService {

  void incrementSmsCount(String userId);

  void incrementEmailCount(String userId);

}
