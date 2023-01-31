package tw.pago.pagobackend.service;


import org.springframework.stereotype.Component;
import tw.pago.pagobackend.dto.ChooseTravelerDto;
import tw.pago.pagobackend.model.Matching;

@Component
public interface MatchingService {

  Matching chooseTraveler(ChooseTravelerDto chooseTravelerDto);

  Matching getMatching(String matchingId);
}
