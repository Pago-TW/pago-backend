package tw.pago.pagobackend.controller;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import tw.pago.pagobackend.dto.ChooseTravelerDto;
import tw.pago.pagobackend.service.MatchingService;

@RestController
public class MatchingController {

  @Autowired
  private MatchingService matchingService;

  @PostMapping("/matching")
  public ResponseEntity<?> chooseTraveler(@RequestBody
      ChooseTravelerDto chooseTravelerDto) {
    matchingService.chooseTraveler(chooseTravelerDto);

    return ResponseEntity.status(HttpStatus.OK).body("");
  }
}
