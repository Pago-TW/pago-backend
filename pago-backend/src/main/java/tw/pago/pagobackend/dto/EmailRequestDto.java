package tw.pago.pagobackend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmailRequestDto {

    private String recipientUserId;// TODO 要在每個寄信件的 Service，都傳入 recipientUserId，弄好之後這裡可以加點Annotation驗證，例如 @NotNull
    private String to;
    private String subject;
    private String body;
    private String recipientName;
    private String contentTitle;
}
