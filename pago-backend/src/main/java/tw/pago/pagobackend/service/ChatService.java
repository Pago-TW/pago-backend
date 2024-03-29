package tw.pago.pagobackend.service;

import java.util.List;
import java.util.Optional;
import tw.pago.pagobackend.dto.ChatroomResponseDto;
import tw.pago.pagobackend.dto.CreateChatRoomRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.MessageResponseDto;
import tw.pago.pagobackend.dto.SendMessageRequestDto;
import tw.pago.pagobackend.dto.UpdateChatroomUserMappingRequestDto;
import tw.pago.pagobackend.model.Chatroom;
import tw.pago.pagobackend.model.ChatroomUserMapping;
import tw.pago.pagobackend.model.Message;
import tw.pago.pagobackend.model.User;

public interface ChatService {

  Message createMessage(SendMessageRequestDto sendMessageRequestDto);

  Chatroom createChatroom(CreateChatRoomRequestDto createChatRoomRequestDto, String currentLoginUserId, String otherUserId);

  Chatroom getChatroomById(String chatroomId);

  ChatroomUserMapping getChatroomUserMappingByUserId(String userId);

  void updateLastReadMessageId(UpdateChatroomUserMappingRequestDto updateChatroomUserMappingRequestDto);

  Optional<Chatroom> findChatroomByUserIds(String userIdA, String userIdB);

  ChatroomResponseDto getChatroomResponseDtoByChatroomAndUser(Chatroom chatroom, User user);

  MessageResponseDto getMessageResponseDtoByMessage(Message message);

  List<ChatroomUserMapping> getChatroomUserMappingListByChatroomId(String chatroomId);

  List<Chatroom> getChatroomList(ListQueryParametersDto listQueryParametersDto);

  List<ChatroomResponseDto> getChatroomResponseDtoListByChatroomListAndUser(List<Chatroom> chatroomList, User user);

  List<Message> getChatHistory(ListQueryParametersDto listQueryParametersDto);

  List<MessageResponseDto> getMessageResponseDtoListByMessageList(List<Message> messageList);

  Integer countChatroom(ListQueryParametersDto listQueryParametersDto);

  Integer countUnreadMessage(String chatroomId, String userId);

  Integer countUnreadMessage(String chatroomId, ChatroomUserMapping chatroomUserMapping);

  Integer countMessage(ListQueryParametersDto listQueryParametersDto);


}
