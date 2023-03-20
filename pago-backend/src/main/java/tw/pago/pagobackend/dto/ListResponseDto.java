package tw.pago.pagobackend.dto;


import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ListResponseDto<T>  {
  private Integer total;
  private Integer startIndex;
  private Integer size;
  private List<T> data;


}
