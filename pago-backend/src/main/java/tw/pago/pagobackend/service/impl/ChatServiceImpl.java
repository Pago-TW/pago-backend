package tw.pago.pagobackend.service.impl;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Collections;
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
import tw.pago.pagobackend.dto.MessageResponseDto;
import tw.pago.pagobackend.dto.SendMessageRequestDto;
import tw.pago.pagobackend.dto.UpdateChatroomUserMappingRequestDto;
import tw.pago.pagobackend.exception.ResourceNotFoundException;
import tw.pago.pagobackend.model.Chatroom;
import tw.pago.pagobackend.model.ChatroomUserMapping;
import tw.pago.pagobackend.model.Message;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.service.ChatService;
import tw.pago.pagobackend.service.UserService;
import tw.pago.pagobackend.util.CurrentUserInfoProvider;
import tw.pago.pagobackend.util.UuidGenerator;

@Service
@AllArgsConstructor
public class ChatServiceImpl implements ChatService {

  private final UuidGenerator uuidGenerator;
  private final MessageDao messageDao;
  private final ChatroomDao chatroomDao;
  private final UserService userService;
  private final ModelMapper modelMapper;
  private final CurrentUserInfoProvider currentUserInfoProvider;

  @Override
  @Transactional
  public Message createMessage(SendMessageRequestDto sendMessageRequestDto) {
    String messageId = uuidGenerator.getUuid();
    String chatroomId = sendMessageRequestDto.getChatroomId();
    String senderId = sendMessageRequestDto.getSenderId();
    MessageTypeEnum messageType = sendMessageRequestDto.getMessageType();



    sendMessageRequestDto.setMessageId(messageId);

    messageDao.createMessage(sendMessageRequestDto);

    // After createMessage, update the last_read_message_id and chatroom.update_date
    UpdateChatroomUserMappingRequestDto updateChatroomUserMappingRequestDto = new UpdateChatroomUserMappingRequestDto();
    updateChatroomUserMappingRequestDto.setChatroomId(chatroomId);
    updateChatroomUserMappingRequestDto.setUserId(senderId);
    updateChatroomUserMappingRequestDto.setLastReadMessageId(messageId);
    chatroomDao.updateLastReadMessageIdByChatroomIdAndUserId(updateChatroomUserMappingRequestDto);
    chatroomDao.updateChatroom(chatroomId);

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
  public void updateLastReadMessageId(
      UpdateChatroomUserMappingRequestDto updateChatroomUserMappingRequestDto) {

    chatroomDao.updateLastReadMessageIdByChatroomIdAndUserId(updateChatroomUserMappingRequestDto);
  }

  @Override
  public Optional<Chatroom> findChatroomByUserIds(String userIdA, String userIdB) {
    return chatroomDao.findChatroomByUserIds(userIdA, userIdB);
  }

  @Override
  public ChatroomResponseDto getChatroomResponseDtoByChatroomAndUser(Chatroom chatroom, User user) {
    String chatroomId = chatroom.getChatroomId();


    // Retrieve the list of ChatroomUserMapping based on the chatroomId
    List<ChatroomUserMapping> chatroomUserMappingList = chatroomDao.getChatroomUserMappingListByChatroomId(chatroom.getChatroomId());

    String loginUserId = user.getUserId();

    // Find the other user's ID using Java Stream and Lambda
    String otherUserId = chatroomUserMappingList.stream()
        .filter(chatroomUserMapping -> !chatroomUserMapping.getUserId().equals(loginUserId))
        .map(ChatroomUserMapping::getUserId)
        .findFirst()
        .orElse(null);

    // Find the login-ed ChatroomUserMapping using Java Stream and Lambda
    ChatroomUserMapping currentLoginUserChatroomUserMapping = chatroomUserMappingList.stream()
        .filter(chatroomUserMapping -> chatroomUserMapping.getUserId().equals(loginUserId))
        .findFirst()
        .orElse(null);

    // Get the other user's information
    User otherUser = userService.getUserById(otherUserId);
    if (otherUser == null) {
      System.out.println("Other user not found");
      throw new ResourceNotFoundException("Other user not found");
    }

    Integer totalUnreadMessage = countUnreadMessage(chatroomId, currentLoginUserChatroomUserMapping);
    List<Message> messageList = messageDao.getMessageListByChatroomId(chatroomId);
    String latestMessageSenderId = "";
    String latestMessageContent = "";
    MessageTypeEnum latestMessageType = null;
    ZonedDateTime latestMessageSendDate = null;

    // Check if messageList is not empty before accessing its elements
    if (!messageList.isEmpty()) {
      latestMessageSenderId = messageList.get(0).getSenderId();
      latestMessageContent = messageList.get(0).getContent();
      latestMessageType = messageList.get(0).getMessageType();
      latestMessageSendDate = messageList.get(0).getSendDate();
    }

    // Create a ChatroomOtherUserDto to store the other user's information
    ChatroomOtherUserDto chatroomOtherUserDto = new ChatroomOtherUserDto();
    chatroomOtherUserDto.setUserId(otherUserId);
    chatroomOtherUserDto.setFullName(otherUser.getFullName());
    chatroomOtherUserDto.setAvatarUrl(otherUser.getAvatarUrl());

    // Create a ChatroomResponseDto to store the chatroom and user information
    ChatroomResponseDto chatroomResponseDto = new ChatroomResponseDto();
    chatroomResponseDto.setChatroomId(chatroomId);
    chatroomResponseDto.setCurrentLoginUserId(loginUserId);
    chatroomResponseDto.setTotalUnreadMessage(totalUnreadMessage);
    chatroomResponseDto.setLatestMessageSenderId(latestMessageSenderId);
    chatroomResponseDto.setLatestMessageSendDate(latestMessageSendDate);
    chatroomResponseDto.setLatestMessageContent(latestMessageContent);
    chatroomResponseDto.setLatestMessageType(latestMessageType);
    chatroomResponseDto.setOtherUser(chatroomOtherUserDto);

    return chatroomResponseDto;
  }

  @Override
  public MessageResponseDto getMessageResponseDtoByMessage(Message message) {

    MessageResponseDto messageResponseDto = modelMapper.map(message, MessageResponseDto.class);

    User sender = userService.getUserById(message.getSenderId());
    String senderFullName = sender.getFullName();
    messageResponseDto.setSenderName(senderFullName);



    return messageResponseDto;
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
  public List<Message> getChatHistory(ListQueryParametersDto listQueryParametersDto) {

    List<Message> messageList = messageDao.getMessageList(listQueryParametersDto);

    if (!messageList.isEmpty()) { // Check if the messageList is not empty
      Message lastReadMessage =  messageList.get(0);
      String lastRaedMessageId = lastReadMessage.getMessageId();
      String chatroomId = lastReadMessage.getChatRoomId();
      String currentLoginUserId = currentUserInfoProvider.getCurrentLoginUserId();

      // After getChatHistory success, update the currentLoginUser's last_read_message_id
      UpdateChatroomUserMappingRequestDto updateChatroomUserMappingRequestDto = new UpdateChatroomUserMappingRequestDto();
      updateChatroomUserMappingRequestDto.setLastReadMessageId(lastRaedMessageId);
      updateChatroomUserMappingRequestDto.setChatroomId(chatroomId);
      updateChatroomUserMappingRequestDto.setUserId(currentLoginUserId);
      chatroomDao.updateLastReadMessageIdByChatroomIdAndUserId(updateChatroomUserMappingRequestDto);
    }

    // The latest messages in the frontend UI will be displayed at the bottom
    Collections.reverse(messageList);

    return messageList;
  }


  @Override
  public List<MessageResponseDto> getMessageResponseDtoListByMessageList(
      List<Message> messageList) {

    List<MessageResponseDto> messageResponseDtoList = messageList.stream()
        .map(this::getMessageResponseDtoByMessage)
        .collect(Collectors.toList());

    return messageResponseDtoList;
  }


  @Override
  public Integer countChatroom(ListQueryParametersDto listQueryParametersDto) {
    return chatroomDao.countChatroom(listQueryParametersDto);
  }

  @Override
  public Integer countUnreadMessage(String chatroomId, String userId) {
    // Get the ChatroomUserMapping for the specified chatroom and user
    ChatroomUserMapping chatroomUserMapping = chatroomDao.getChatroomUserMappingByChatroomIdAndUserId(chatroomId, userId);

    // Get the last read message ID from the ChatroomUserMapping
    String lastReadMessageId = chatroomUserMapping.getLastReadMessageId();

    // Count the messages in the chatroom that were sent after the last read message
    int unreadMessageCount = messageDao.countMessagesAfterMessageId(chatroomId, lastReadMessageId);

    return unreadMessageCount;
  }

  @Override
  public Integer countUnreadMessage(String chatroomId, ChatroomUserMapping chatroomUserMapping) {
    // Get the last read message ID from the ChatroomUserMapping
    String lastReadMessageId = chatroomUserMapping.getLastReadMessageId();

    // Count the messages in the chatroom that were sent after the last read message
    int unreadMessageCount = messageDao.countMessagesAfterMessageId(chatroomId, lastReadMessageId);

    return unreadMessageCount;
  }

  @Override
  public Integer countMessage(ListQueryParametersDto listQueryParametersDto) {

    return messageDao.countMessage(listQueryParametersDto);
  }
}
