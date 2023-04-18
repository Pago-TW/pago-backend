package tw.pago.pagobackend.dao;

import tw.pago.pagobackend.dto.SendMessageRequestDto;
import tw.pago.pagobackend.model.Message;

public interface MessageDao {

  void createMessage(SendMessageRequestDto sendMessageRequestDto);

  Message getMessageById(String messageId);

}
