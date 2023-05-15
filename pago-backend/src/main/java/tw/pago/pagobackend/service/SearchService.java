package tw.pago.pagobackend.service;

import java.util.List;

import tw.pago.pagobackend.dto.ListResponseDto;
import tw.pago.pagobackend.dto.SearchQueryParametersDto;
import tw.pago.pagobackend.dto.SearchResultDto;

public interface SearchService {
    ListResponseDto<?> search(SearchQueryParametersDto searchQueryParametersDto);
}
