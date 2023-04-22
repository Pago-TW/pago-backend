package tw.pago.pagobackend.dao;

import java.util.List;
import java.util.Optional;
import tw.pago.pagobackend.dto.CreateChatRoomRequestDto;
import tw.pago.pagobackend.dto.CreateChatRoomUserMappingRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.model.Chatroom;
import tw.pago.pagobackend.model.ChatroomUserMapping;

public interface ChatroomDao {

  void createChatroom(CreateChatRoomRequestDto createChatRoomRequestDto);

  Chatroom getChatroomById(String chatroomId);

  void createChatroomUserMapping(CreateChatRoomUserMappingRequestDto createChatRoomUserMappingRequestDto);

  ChatroomUserMapping getChatroomUserMappingByUserId(String userId);

  List<ChatroomUserMapping> getChatroomUserMappingListByChatroomId(String chatroomId);

  Optional<Chatroom> findChatroomByUserIds(String userIdA, String userIdB);

  List<Chatroom> getChatroomList(ListQueryParametersDto listQueryParametersDto);

  Integer countChatroom(ListQueryParametersDto listQueryParametersDto);

  boolean isChatroomUserMappingExists(String chatroomId, String userId);


}
