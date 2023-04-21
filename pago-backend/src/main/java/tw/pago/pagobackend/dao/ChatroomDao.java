package tw.pago.pagobackend.dao;

import java.util.Optional;
import tw.pago.pagobackend.dto.CreateChatRoomRequestDto;
import tw.pago.pagobackend.dto.CreateChatRoomUserMappingRequestDto;
import tw.pago.pagobackend.model.Chatroom;
import tw.pago.pagobackend.model.ChatroomUserMapping;

public interface ChatroomDao {

  void createChatroom(CreateChatRoomRequestDto createChatRoomRequestDto);

  Chatroom getChatroomById(String chatroomId);

  void createChatroomUserMapping(CreateChatRoomUserMappingRequestDto createChatRoomUserMappingRequestDto);

  ChatroomUserMapping getChatroomUserMappingByUserId(String userId);

  Optional<Chatroom> findChatroomByUserIds(String userIdA, String userIdB);

}
