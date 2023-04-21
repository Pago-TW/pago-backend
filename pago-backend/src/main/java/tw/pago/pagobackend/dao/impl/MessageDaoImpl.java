package tw.pago.pagobackend.dao.impl;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import tw.pago.pagobackend.dao.MessageDao;
import tw.pago.pagobackend.dto.SendMessageRequestDto;
import tw.pago.pagobackend.model.Message;
import tw.pago.pagobackend.rowmapper.MessageRowMapper;

@Repository
@AllArgsConstructor
public class MessageDaoImpl implements MessageDao {

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Override
  public void createMessage(SendMessageRequestDto sendMessageRequestDto) {
    String sql = "INSERT INTO message (message_id, chatroom_id, sender_id, content, message_type, send_date) "
        + "VALUES (:messageId, :chatroomId, :senderId, :content, :messageType, :sendDate)";

    Map<String, Object> map = new HashMap<>();

    LocalDateTime now = LocalDateTime.now();

    map.put("messageId", sendMessageRequestDto.getMessageId());
    map.put("chatroomId", sendMessageRequestDto.getChatRoomId());
    map.put("senderId", sendMessageRequestDto.getSenderId());
    map.put("content", sendMessageRequestDto.getContent());
    map.put("messageType", sendMessageRequestDto.getMessageType().name());
    map.put("sendDate", now);

    namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map));

  }

  @Override
  public Message getMessageById(String messageId) {
    String sql = "SELECT message_id, chatroom_id, sender_id, content, message_type, send_date "
        + "FROM message "
        + "WHERE message_id = :messageId ";

    Map<String, Object> map = new HashMap<>();
    map.put("messageId", messageId);


    List<Message> messageList = namedParameterJdbcTemplate.query(sql, map, new MessageRowMapper());

    if (messageList.size() > 0) {
      return messageList.get(0);
    } else {
      return null;
    }
  }
}
