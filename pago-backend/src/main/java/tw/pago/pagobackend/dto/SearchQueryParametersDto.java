package tw.pago.pagobackend.dto;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class SearchQueryParametersDto {
  private Integer startIndex;
  private Integer size;
  private String orderBy;
  private String sort;

  private String type;
  private String query;
}
