package tw.pago.pagobackend.service;

import java.util.Optional;
import tw.pago.pagobackend.dto.CreateChatRoomRequestDto;
import tw.pago.pagobackend.dto.SendMessageRequestDto;
import tw.pago.pagobackend.model.Chatroom;
import tw.pago.pagobackend.model.ChatroomUserMapping;
import tw.pago.pagobackend.model.Message;

public interface ChatService {

  Message createMessage(SendMessageRequestDto sendMessageRequestDto);

  Chatroom createChatroom(CreateChatRoomRequestDto createChatRoomRequestDto, String currentLoginUserId, String toUserId);

  Chatroom getChatroomById(String chatroomId);

  ChatroomUserMapping getChatroomUserMappingByUserId(String userId);

  Optional<Chatroom> findChatroomByUserIds(String userIdA, String userIdB);


}
