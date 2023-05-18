package tw.pago.pagobackend.dao;

public interface DailyCountDao {

  void updateSmsCount(String userId);

  void updateEmailCount(String userId);

}
