package tw.pago.pagobackend.dao.impl;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import tw.pago.pagobackend.dao.MessageDao;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
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

    ZonedDateTime now = ZonedDateTime.now();
    Timestamp timestamp = Timestamp.from(now.toInstant());

    map.put("messageId", sendMessageRequestDto.getMessageId());
    map.put("chatroomId", sendMessageRequestDto.getChatroomId());
    map.put("senderId", sendMessageRequestDto.getSenderId());
    map.put("content", sendMessageRequestDto.getContent());
    map.put("messageType", sendMessageRequestDto.getMessageType().name());
    map.put("sendDate", timestamp);

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

  @Override
  public List<Message> getMessageList(ListQueryParametersDto listQueryParametersDto) {
    String sql = "SELECT message_id, chatroom_id, sender_id, content, message_type, send_date "
        + "FROM message "
        + "WHERE 1=1 ";

    Map<String, Object> map = new HashMap<>();


    // Filtering e.g. chatroomId, search
    sql = addFilteringSql(sql, map, listQueryParametersDto);

    // Order by {column} & sort by {DESC/ASC}
    sql = sql + " ORDER BY " + listQueryParametersDto.getOrderBy() + " " + listQueryParametersDto.getSort();

    // Pagination
    sql = sql + " LIMIT :size OFFSET :startIndex ";
    map.put("size", listQueryParametersDto.getSize());
    map.put("startIndex", listQueryParametersDto.getStartIndex());

    List<Message> messageList = namedParameterJdbcTemplate.query(sql, map, new MessageRowMapper());

    return messageList;
  }

  @Override
  public List<Message> getMessageListByChatroomId(String chatroomId) {
    String sql = "SELECT message_id, chatroom_id, sender_id, content, message_type, send_date "
        + "FROM message "
        + "WHERE chatroom_id = :chatroomId "
        + "ORDER BY send_date DESC ";

    Map<String, Object> map = new HashMap<>();
    map.put("chatroomId", chatroomId);

    List<Message> messageList = namedParameterJdbcTemplate.query(sql, map, new MessageRowMapper());

    return messageList;
  }

  @Override
  public Integer countMessage(ListQueryParametersDto listQueryParametersDto) {
    String sql = "SELECT COUNT(message_id) "
        + "FROM message "
        + "WHERE 1=1 ";

    Map<String, Object> map = new HashMap<>();

    // Filtering e.g. status, search
    sql = addFilteringSql(sql, map, listQueryParametersDto);

    Integer total = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);

    return total;
  }

  @Override
  public Integer countMessagesAfterMessageId(String chatroomId, String lastReadMessageId) {
    // Base SQL query to count messages in the chatroom
    String sql = "SELECT COUNT(*) "
        + "FROM message "
        + "WHERE chatroom_id = :chatroomId ";

    // Additional SQL condition to count messages only after the last read message
    String lastReadMessageCondition = "AND send_date > ("
        + "    SELECT send_date "
        + "    FROM message "
        + "    WHERE message_id = :lastReadMessageId "
        + "  )";

    // Check for a null last_read_message_id since the chatroom may have just been created,
    // in which case we don't add the lastReadMessageCondition
    sql = sql + (lastReadMessageId == null ? "" : lastReadMessageCondition);

    Map<String, Object> map = new HashMap<>();
    map.put("chatroomId", chatroomId);
    map.put("lastReadMessageId", lastReadMessageId);

    Integer total = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);

    return total;
  }

  private String addFilteringSql(String sql, Map<String, Object> map, ListQueryParametersDto listQueryParametersDto) {


    if (listQueryParametersDto.getSearch() != null) {
      sql = sql + " AND content LIKE :search ";
      map.put("search", "%" + listQueryParametersDto.getSearch() + "%");
    }

    if (listQueryParametersDto.getChatroomId() != null) {
      sql = sql + " AND chatroom_id = :chatroomId ";
      map.put("chatroomId", listQueryParametersDto.getChatroomId());
    }


    return sql;
  }
}
