package tw.pago.pagobackend.dao;


import tw.pago.pagobackend.dto.ChooseTravelerDto;
import tw.pago.pagobackend.model.Matching;

public interface MatchingDao {

  void createMatching(ChooseTravelerDto chooseTravelerDto);

  Matching getMatching(String matchingId);

}
