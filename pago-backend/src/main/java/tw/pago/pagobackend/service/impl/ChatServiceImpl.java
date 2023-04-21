package tw.pago.pagobackend.service.impl;

import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.pago.pagobackend.dao.ChatroomDao;
import tw.pago.pagobackend.dao.MessageDao;
import tw.pago.pagobackend.dto.CreateChatRoomRequestDto;
import tw.pago.pagobackend.dto.CreateChatRoomUserMappingRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.SendMessageRequestDto;
import tw.pago.pagobackend.model.Chatroom;
import tw.pago.pagobackend.model.ChatroomUserMapping;
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

    messageDao.createMessage(sendMessageRequestDto);

    Message message = messageDao.getMessageById(messageId);

    return message;
  }

  @Override
  @Transactional
  public Chatroom createChatroom(CreateChatRoomRequestDto createChatRoomRequestDto,
      String currentLoginUserId, String otherUserId) {
    String chatroomId = uuidGenerator.getUuid();
    String loginUserChatroomUserMappingId = uuidGenerator.getUuid();
    String otherUserChatroomUserMappingId = uuidGenerator.getUuid();



    // Create Chatroom
    createChatRoomRequestDto.setChatroomId(chatroomId);
    chatroomDao.createChatroom(createChatRoomRequestDto);

    // Create currentLoginUser's chatroomUserMapping
    CreateChatRoomUserMappingRequestDto loginUserCreateChatRoomUserMappingRequestDto = new CreateChatRoomUserMappingRequestDto();
    loginUserCreateChatRoomUserMappingRequestDto.setChatroomUserMappingId(loginUserChatroomUserMappingId);
    loginUserCreateChatRoomUserMappingRequestDto.setUserId(currentLoginUserId);
    loginUserCreateChatRoomUserMappingRequestDto.setChatroomId(chatroomId);
    chatroomDao.createChatroomUserMapping(loginUserCreateChatRoomUserMappingRequestDto);

    // Create toUser's chatroomUserMapping
    CreateChatRoomUserMappingRequestDto otherUserChatRoomUserMappingRequestDto = new CreateChatRoomUserMappingRequestDto();
    otherUserChatRoomUserMappingRequestDto.setChatroomUserMappingId(otherUserChatroomUserMappingId);
    otherUserChatRoomUserMappingRequestDto.setUserId(otherUserId);
    otherUserChatRoomUserMappingRequestDto.setChatroomId(chatroomId);
    chatroomDao.createChatroomUserMapping(otherUserChatRoomUserMappingRequestDto);

    // Get Chatroom

    Chatroom chatroom = chatroomDao.getChatroomById(chatroomId);



    return chatroom;
  }

  @Override
  public Chatroom getChatroomById(String chatroomId) {

    return chatroomDao.getChatroomById(chatroomId);
  }

  @Override
  public ChatroomUserMapping getChatroomUserMappingByUserId(String userId) {
    return chatroomDao.getChatroomUserMappingByUserId(userId);
  }

  @Override
  public Optional<Chatroom> findChatroomByUserIds(String userIdA, String userIdB) {
    return chatroomDao.findChatroomByUserIds(userIdA, userIdB);
  }

  @Override
  public List<Chatroom> getChatroomList(ListQueryParametersDto listQueryParametersDto) {


    return chatroomDao.getChatroomList(listQueryParametersDto);
  }

  @Override
  public Integer countChatroom(ListQueryParametersDto listQueryParametersDto) {
    return chatroomDao.countChatroom(listQueryParametersDto);
  }
}
