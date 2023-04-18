package tw.pago.pagobackend.service;

import tw.pago.pagobackend.dto.SendMessageRequestDto;
import tw.pago.pagobackend.model.Message;

public interface ChatService {

  Message createMessage(SendMessageRequestDto sendMessageRequestDto);


}
