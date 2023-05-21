package tw.pago.pagobackend.dto;

import java.util.List;

import lombok.Data;
import lombok.NoArgsConstructor;
import tw.pago.pagobackend.model.Order;
import tw.pago.pagobackend.model.Trip;
import tw.pago.pagobackend.model.User;

@Data
@NoArgsConstructor
public class SearchResultDto {
    private List<Order> orders;
    private List<Trip> trips;
    private List<User> users;
}
