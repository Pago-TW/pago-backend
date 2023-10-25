package tw.pago.pagobackend.service;

import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.ListResponseDto;

public interface SearchService {
    // ListResponseDto<?> search(SearchQueryParametersDto searchQueryParametersDto);
    ListResponseDto<?> search(ListQueryParametersDto listQueryParametersDto, String type);

}
