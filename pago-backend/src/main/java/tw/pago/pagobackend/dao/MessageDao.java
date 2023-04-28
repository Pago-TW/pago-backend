package tw.pago.pagobackend.dao;

import java.util.List;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.SendMessageRequestDto;
import tw.pago.pagobackend.model.Message;

public interface MessageDao {

  void createMessage(SendMessageRequestDto sendMessageRequestDto);

  Message getMessageById(String messageId);

  List<Message> getMessageList(ListQueryParametersDto listQueryParametersDto);

  List<Message> getMessageListByChatroomId(String chatroomId);

  Integer countMessage(ListQueryParametersDto listQueryParametersDto);

  Integer countMessagesAfterMessageId(String chatroomId, String lastReadMessageId);

}
