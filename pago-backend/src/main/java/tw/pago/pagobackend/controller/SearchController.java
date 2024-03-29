package tw.pago.pagobackend.controller;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.AllArgsConstructor;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.ListResponseDto;
import tw.pago.pagobackend.service.SearchService;

@RestController
@AllArgsConstructor
public class SearchController {

  private final SearchService searchService;

  @GetMapping("/search")
  public ResponseEntity<?> search(
      @RequestParam(defaultValue = "0") @Min(0) Integer startIndex,
      @RequestParam(defaultValue = "10") @Min(0) @Max(100) Integer size,
      @RequestParam(defaultValue = "create_date") String orderBy,
      @RequestParam(defaultValue = "DESC") String sort,
      @RequestParam(defaultValue = "order") String type,
      @RequestParam(defaultValue = "") String query) {

    ListQueryParametersDto listQueryParametersDto = ListQueryParametersDto.builder()
        .search(query)
        .startIndex(startIndex)
        .size(size)
        .orderBy(orderBy)
        .sort(sort)
        .build();

    ListResponseDto<?> result = searchService.search(listQueryParametersDto, type);

    
    return ResponseEntity.ok().body(result);
  }
}
