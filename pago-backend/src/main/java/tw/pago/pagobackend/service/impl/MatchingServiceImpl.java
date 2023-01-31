package tw.pago.pagobackend.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tw.pago.pagobackend.constant.MatchingStatusEnum;
import tw.pago.pagobackend.dao.MatchingDao;
import tw.pago.pagobackend.dto.ChooseTravelerDto;
import tw.pago.pagobackend.model.Matching;
import tw.pago.pagobackend.service.MatchingService;
import tw.pago.pagobackend.util.UuidGenerator;

@Service
public class MatchingServiceImpl implements MatchingService {

  @Autowired
  private MatchingDao matchingDao;

  @Autowired
  private UuidGenerator uuidGenerator;

  @Override
  public Matching chooseTraveler(ChooseTravelerDto chooseTravelerDto) {

    String matchingUuid = uuidGenerator.getUuid();
    MatchingStatusEnum matchingInitialStatus = MatchingStatusEnum.TO_BE_PURCHASED;
    chooseTravelerDto.setMatchingId(matchingUuid);
    chooseTravelerDto.setMatchingStatus(matchingInitialStatus);

    matchingDao.createMatching(chooseTravelerDto);

    Matching matching = matchingDao.getMatching(matchingUuid);
    return matching;
  }

  @Override
  public Matching getMatching(String matchingId) {
    return matchingDao.getMatching(matchingId);
  }
}
