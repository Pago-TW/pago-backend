package tw.pago.pagobackend.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tw.pago.pagobackend.service.DailyCountService;

@Service
@AllArgsConstructor
public class DailyCountServiceImpl implements DailyCountService {


  @Override
  public void incrementSmsCount(String userId) {

  }

  @Override
  public void incrementEmailCount(String userId) {

  }
}
