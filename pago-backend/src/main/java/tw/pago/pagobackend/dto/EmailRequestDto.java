package tw.pago.pagobackend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailRequestDto {

    private String to;
    private String subject;
    private String body;
    private String recipientName;
}
