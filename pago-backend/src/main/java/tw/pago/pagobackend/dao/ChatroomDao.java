package tw.pago.pagobackend.dao;

import tw.pago.pagobackend.dto.CreateChatRoomRequestDto;
import tw.pago.pagobackend.model.Chatroom;

public interface ChatroomDao {

  void createChatroom(CreateChatRoomRequestDto createChatRoomRequestDto);

  Chatroom getChatroomById(String chatroomId);

  void createChatroomUserMapping(CreateChatRoomRequestDto createChatRoomRequestDto);

}
