package tw.pago.pagobackend.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tw.pago.pagobackend.dto.ChooseTravelerDto;
import tw.pago.pagobackend.model.Matching;
import tw.pago.pagobackend.service.MatchingService;

@RestController
public class MatchingController {

  @Autowired
  private MatchingService matchingService;

  @PostMapping("/matchings")
  public ResponseEntity<Matching> chooseTraveler(@RequestBody ChooseTravelerDto chooseTravelerDto) {
    Matching matching = matchingService.chooseTraveler(chooseTravelerDto);


    return ResponseEntity.status(HttpStatus.CREATED).body(matching);
  }

  @GetMapping ("/matchings/{matchingId}")
  public ResponseEntity<Matching> getMatching(@PathVariable String matchingId) {
    Matching matching = matchingService.getMatching(matchingId);
    
    return ResponseEntity.status(HttpStatus.OK).body(matching);
  }
}
