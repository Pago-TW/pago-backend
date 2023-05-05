package tw.pago.pagobackend.dto;


import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ListResponseDto<T>  {
  private Integer total;
  private Integer startIndex;
  private Integer size;
  private OrderResponseDto order;
  private List<T> data;


}
