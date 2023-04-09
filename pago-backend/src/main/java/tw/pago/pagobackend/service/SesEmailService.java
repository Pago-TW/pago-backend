package tw.pago.pagobackend.service;

import tw.pago.pagobackend.dto.EmailRequestDto;

public interface SesEmailService {
    void sendEmail(EmailRequestDto emailRequestDto);
}
