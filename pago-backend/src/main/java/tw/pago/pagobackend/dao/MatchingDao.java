package tw.pago.pagobackend.dao;


import tw.pago.pagobackend.dto.ChooseTravelerDto;

public interface MatchingDao {

  void createMatching(ChooseTravelerDto chooseTravelerDto);

}
