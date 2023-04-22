package tw.pago.pagobackend.service.impl;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tw.pago.pagobackend.constant.MessageTypeEnum;
import tw.pago.pagobackend.dao.ChatroomDao;
import tw.pago.pagobackend.dao.MessageDao;
import tw.pago.pagobackend.dto.ChatroomOtherUserDto;
import tw.pago.pagobackend.dto.ChatroomResponseDto;
import tw.pago.pagobackend.dto.CreateChatRoomRequestDto;
import tw.pago.pagobackend.dto.CreateChatRoomUserMappingRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.SendMessageRequestDto;
import tw.pago.pagobackend.exception.ResourceNotFoundException;
import tw.pago.pagobackend.model.Chatroom;
import tw.pago.pagobackend.model.ChatroomUserMapping;
import tw.pago.pagobackend.model.Message;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.service.ChatService;
import tw.pago.pagobackend.service.UserService;
import tw.pago.pagobackend.util.UuidGenerator;

@Service
@AllArgsConstructor
public class ChatServiceImpl implements ChatService {

  private final UuidGenerator uuidGenerator;
  private final MessageDao messageDao;
  private final ChatroomDao chatroomDao;
  private final UserService userService;
  private final ModelMapper modelMapper;

  @Override
  public Message createMessage(SendMessageRequestDto sendMessageRequestDto) {
    String messageId = uuidGenerator.getUuid();
    MessageTypeEnum messageType = sendMessageRequestDto.getMessageType();



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

    // Check isChatroomUserMappingExists
    if (!chatroomDao.isChatroomUserMappingExists(chatroomId, currentLoginUserId)) {
      // Create currentLoginUser's chatroomUserMapping
      CreateChatRoomUserMappingRequestDto loginUserCreateChatRoomUserMappingRequestDto = new CreateChatRoomUserMappingRequestDto();
      loginUserCreateChatRoomUserMappingRequestDto.setChatroomUserMappingId(loginUserChatroomUserMappingId);
      loginUserCreateChatRoomUserMappingRequestDto.setUserId(currentLoginUserId);
      loginUserCreateChatRoomUserMappingRequestDto.setChatroomId(chatroomId);
      chatroomDao.createChatroomUserMapping(loginUserCreateChatRoomUserMappingRequestDto);
    }

    // Check isChatroomUserMappingExists
    if (!chatroomDao.isChatroomUserMappingExists(chatroomId, otherUserId)) {
      // Create otherUser's chatroomUserMapping
      CreateChatRoomUserMappingRequestDto otherUserChatRoomUserMappingRequestDto = new CreateChatRoomUserMappingRequestDto();
      otherUserChatRoomUserMappingRequestDto.setChatroomUserMappingId(otherUserChatroomUserMappingId);
      otherUserChatRoomUserMappingRequestDto.setUserId(otherUserId);
      otherUserChatRoomUserMappingRequestDto.setChatroomId(chatroomId);
      chatroomDao.createChatroomUserMapping(otherUserChatRoomUserMappingRequestDto);
    }

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
  public ChatroomResponseDto getChatroomResponseDtoByChatroomAndUser(Chatroom chatroom, User user) {
    List<ChatroomUserMapping> chatroomUserMappingList = chatroomDao.getChatroomUserMappingListByChatroomId(chatroom.getChatroomId());

    String loginUserId = user.getUserId();
    String otherUserId = null;


    for (ChatroomUserMapping chatroomUserMapping: chatroomUserMappingList) {
      if (!chatroomUserMapping.getUserId().equals(loginUserId)) {
        otherUserId = chatroomUserMapping.getUserId();
        break;
      }
    }

    User otherUser = userService.getUserById(otherUserId);
    if (otherUser == null) {
      throw new ResourceNotFoundException("Other user not found");
    }

    ChatroomOtherUserDto chatroomOtherUserDto = new ChatroomOtherUserDto();
    chatroomOtherUserDto.setUserId(otherUserId);
    chatroomOtherUserDto.setFullName(otherUser.getFullName());
    chatroomOtherUserDto.setAvatarUrl(otherUser.getAvatarUrl());


    ChatroomResponseDto chatroomResponseDto = new ChatroomResponseDto();
    chatroomResponseDto.setChatroomId(chatroom.getChatroomId());
    chatroomResponseDto.setCurrentLoginUserId(user.getUserId());
    chatroomResponseDto.setTotalUnreadMessage(0); // TODO 根據目前使用者做出他的unreadMessage
    chatroomResponseDto.setUpdateDate(chatroom.getUpdateDate());
    chatroomResponseDto.setLatestMessageContent("// TODO LatestMessageContent"); // TODO 撈出該聊天室LatestMessageContent
    chatroomResponseDto.setOtherUser(chatroomOtherUserDto);

    return chatroomResponseDto;
  }

  @Override
  public List<ChatroomUserMapping> getChatroomUserMappingListByChatroomId(String chatroomId) {
    return chatroomDao.getChatroomUserMappingListByChatroomId(chatroomId);
  }

  @Override
  public List<Chatroom> getChatroomList(ListQueryParametersDto listQueryParametersDto) {


    return chatroomDao.getChatroomList(listQueryParametersDto);
  }

  @Override
  public List<ChatroomResponseDto> getChatroomResponseDtoListByChatroomListAndUser(
      List<Chatroom> chatroomList, User user) {

    List<ChatroomResponseDto> chatroomResponseDtoList = chatroomList.stream()
        .map(chatroom -> getChatroomResponseDtoByChatroomAndUser(chatroom, user))
        .collect(Collectors.toList());

    return chatroomResponseDtoList;
  }


  @Override
  public Integer countChatroom(ListQueryParametersDto listQueryParametersDto) {
    return chatroomDao.countChatroom(listQueryParametersDto);
  }
}
