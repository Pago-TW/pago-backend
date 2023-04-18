package tw.pago.pagobackend.service.impl;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import tw.pago.pagobackend.dao.ChatroomDao;
import tw.pago.pagobackend.dao.MessageDao;
import tw.pago.pagobackend.dto.CreateChatRoomRequestDto;
import tw.pago.pagobackend.dto.SendMessageRequestDto;
import tw.pago.pagobackend.model.Chatroom;
import tw.pago.pagobackend.model.Message;
import tw.pago.pagobackend.service.ChatService;
import tw.pago.pagobackend.util.UuidGenerator;

@Service
@AllArgsConstructor
public class ChatServiceImpl implements ChatService {

  private final UuidGenerator uuidGenerator;
  private final MessageDao messageDao;
  private final ChatroomDao chatroomDao;

  @Override
  public Message createMessage(SendMessageRequestDto sendMessageRequestDto) {
    String messageId = uuidGenerator.getUuid();
    sendMessageRequestDto.setMessageId(messageId);

    Message message = messageDao.getMessageById(messageId);

    return message;
  }

  @Override
  public Chatroom createChatRoom(CreateChatRoomRequestDto createChatRoomRequestDto) {
    String chatroomId = uuidGenerator.getUuid();


    createChatRoomRequestDto.setChatroomId(chatroomId);
    createChatRoomRequestDto.getCreateChatRoomUserMappingRequestDto().setChatroomId(chatroomId);

    chatroomDao.createChatroom(createChatRoomRequestDto);
    chatroomDao.createChatroomUserMapping(createChatRoomRequestDto);


    return null;
  }
}
