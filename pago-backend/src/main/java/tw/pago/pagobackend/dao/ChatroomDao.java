package tw.pago.pagobackend.dao;

import tw.pago.pagobackend.dto.CreateChatRoomRequestDto;
import tw.pago.pagobackend.model.Chatroom;
import tw.pago.pagobackend.model.ChatroomUserMapping;

public interface ChatroomDao {

  void createChatroom(CreateChatRoomRequestDto createChatRoomRequestDto);

  Chatroom getChatroomById(String chatroomId);

  void createChatroomUserMapping(CreateChatRoomRequestDto createChatRoomRequestDto);

}
