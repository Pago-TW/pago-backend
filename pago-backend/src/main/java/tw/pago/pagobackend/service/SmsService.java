package tw.pago.pagobackend.service;

import tw.pago.pagobackend.dto.SmsRequestDto;

public interface SmsService {
    void sendSms(SmsRequestDto smsRequestDto);

}
