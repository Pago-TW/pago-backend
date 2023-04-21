package tw.pago.pagobackend.dao.impl;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import tw.pago.pagobackend.dao.ChatroomDao;
import tw.pago.pagobackend.dto.CreateChatRoomRequestDto;
import tw.pago.pagobackend.model.Chatroom;
import tw.pago.pagobackend.rowmapper.ChatroomRowMapper;

@Repository
@AllArgsConstructor
public class ChatroomDaoImpl implements ChatroomDao {

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Override
  public void createChatroom(CreateChatRoomRequestDto createChatRoomRequestDto) {
    String sql = "INSERT INTO chatroom (chatroom_id, create_date, update_date) "
        + "VALUES (:chatroomId, :createDate, :updateDate)";

    Map<String, Object> map = new HashMap<>();

    LocalDate now = LocalDate.now();

    map.put("chatroomId", createChatRoomRequestDto.getChatroomId());
    map.put("createDate", now);
    map.put("updateDate", now);

    namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map));
  }

  @Override
  public Chatroom getChatroomById(String chatroomId) {
    String sql = "SELECT chatroom_id, create_date, update_date "
        + "FROM chatroom "
        + "WHERE chatroom_id = :chatroomId ";

    Map<String, Object> map = new HashMap<>();
    map.put("chatroomId", chatroomId);


    List<Chatroom> chatroomList = namedParameterJdbcTemplate.query(sql, map, new ChatroomRowMapper());

    if (chatroomList.size() > 0) {
      return chatroomList.get(0);
    } else {
      return null;
    }
  }

  @Override
  public void createChatroomUserMapping(CreateChatRoomRequestDto createChatRoomRequestDto) {
    String sql = "INSERT INTO chatroom_user_mapping (chatroom_user_mapping_id, chatroom_id, user_id, create_date, update_date) "
        + "VALUES (:chatroomUserMappingId, :chatroomId, :userId, :createDate, :updateDate)";

    Map<String, Object> map = new HashMap<>();

    LocalDate now = LocalDate.now();
    map.put("chatroomUserMappingId", createChatRoomRequestDto.getCreateChatRoomUserMappingRequestDto().getChatroomUserMappingId());
    map.put("chatroomId", createChatRoomRequestDto.getCreateChatRoomUserMappingRequestDto().getChatroomId());
    map.put("userId", createChatRoomRequestDto.getCreateChatRoomUserMappingRequestDto().getUserId());
    map.put("createDate", now);
    map.put("updateDate", now);

    namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map));
  }
}
