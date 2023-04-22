package tw.pago.pagobackend.dao.impl;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;
import tw.pago.pagobackend.dao.ChatroomDao;
import tw.pago.pagobackend.dto.CreateChatRoomRequestDto;
import tw.pago.pagobackend.dto.CreateChatRoomUserMappingRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.model.Chatroom;
import tw.pago.pagobackend.model.ChatroomUserMapping;
import tw.pago.pagobackend.rowmapper.ChatroomRowMapper;
import tw.pago.pagobackend.rowmapper.ChatroomUserMappingRowMapper;

@Repository
@AllArgsConstructor
public class ChatroomDaoImpl implements ChatroomDao {

  private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

  @Override
  public void createChatroom(CreateChatRoomRequestDto createChatRoomRequestDto) {
    String sql = "INSERT INTO chatroom (chatroom_id, create_date, update_date) "
        + "VALUES (:chatroomId, :createDate, :updateDate)";

    Map<String, Object> map = new HashMap<>();

    LocalDateTime now = LocalDateTime.now();

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
  public void createChatroomUserMapping(
      CreateChatRoomUserMappingRequestDto createChatRoomUserMappingRequestDto) {
    String sql = "INSERT INTO chatroom_user_mapping (chatroom_user_mapping_id, chatroom_id, user_id, last_read_message_id, create_date, update_date) "
        + "VALUES (:chatroomUserMappingId, :chatroomId, :userId, :lastReadMessageId, :createDate, :updateDate)";

    Map<String, Object> map = new HashMap<>();

    LocalDateTime now = LocalDateTime.now();
    map.put("chatroomUserMappingId", createChatRoomUserMappingRequestDto.getChatroomUserMappingId());
    map.put("chatroomId", createChatRoomUserMappingRequestDto.getChatroomId());
    map.put("userId", createChatRoomUserMappingRequestDto.getUserId());
    map.put("lastReadMessageId", null);
    map.put("createDate", now);
    map.put("updateDate", now);

    namedParameterJdbcTemplate.update(sql, new MapSqlParameterSource(map));
  }

  @Override
  public ChatroomUserMapping getChatroomUserMappingByUserId(String userId) {
    String sql = "SELECT chatroom_user_mapping_id, chatroom_id, user_id, last_read_message_id, create_date, update_date "
        + "FROM chatroom_user_mapping "
        + "WHERE user_id = :userId ";

    Map<String, Object> map = new HashMap<>();
    map.put("userId", userId);


    List<ChatroomUserMapping> chatroomUserMappingList = namedParameterJdbcTemplate.query(sql, map, new ChatroomUserMappingRowMapper());

    if (chatroomUserMappingList.size() > 0) {
      return chatroomUserMappingList.get(0);
    } else {
      return null;
    }
  }

  @Override
  public List<ChatroomUserMapping> getChatroomUserMappingListByChatroomId(String chatroomId) {
    String sql = "SELECT chatroom_user_mapping_id, chatroom_id, user_id, last_read_message_id, create_date, update_date "
        + "FROM chatroom_user_mapping "
        + "WHERE chatroom_id = :chatroomId ";

    Map<String, Object> map = new HashMap<>();
    map.put("chatroomId", chatroomId);

    List<ChatroomUserMapping> chatroomUserMappingList = namedParameterJdbcTemplate.query(sql, map, new ChatroomUserMappingRowMapper());

    return chatroomUserMappingList;

  }

  @Override
  public Optional<Chatroom> findChatroomByUserIds(String userIdA, String userIdB) {
    String sql = "SELECT cr.chatroom_id, cr.create_date, cr.update_date "
        + "FROM chatroom AS cr "
        + "JOIN chatroom_user_mapping AS cumA ON cr.chatroom_id = cumA.chatroom_id AND cumA.user_id = :userIdA "
        + "JOIN chatroom_user_mapping AS cumB ON cr.chatroom_id = cumB.chatroom_id AND cumB.user_id = :userIdB ";


    Map<String, Object> map = new HashMap<>();
    map.put("userIdA", userIdA);
    map.put("userIdB", userIdB);


    List<Chatroom> chatroomList = namedParameterJdbcTemplate.query(sql, map, new ChatroomRowMapper());

    if (chatroomList.size() > 0) {
      return Optional.ofNullable(chatroomList.get(0));
    } else {
      return Optional.empty();
    }
  }

  @Override
  public List<Chatroom> getChatroomList(ListQueryParametersDto listQueryParametersDto) {
    String sql = "SELECT DISTINCT cr.chatroom_id, cr.create_date, cr.update_date "
        + "FROM chatroom AS cr "
        + "JOIN chatroom_user_mapping AS cum ON cr.chatroom_id = cum.chatroom_id "
        + "WHERE 1=1 ";

    Map<String, Object> map = new HashMap<>();

    // Filtering e.g. currentLoginUserId, search
    sql = addFilteringSql(sql, map, listQueryParametersDto);

    // Order by {column} & sort by {DESC/ASC}
    sql = sql + " ORDER BY " + listQueryParametersDto.getOrderBy() + " " + listQueryParametersDto.getSort();

    // Pagination
    sql = sql + " LIMIT :size OFFSET :startIndex ";
    map.put("size", listQueryParametersDto.getSize());
    map.put("startIndex", listQueryParametersDto.getStartIndex());


    List<Chatroom> chatroomList = namedParameterJdbcTemplate.query(sql, map, new ChatroomRowMapper());

    return chatroomList;
  }

  @Override
  public Integer countChatroom(ListQueryParametersDto listQueryParametersDto) {
    String sql = "SELECT COUNT(DISTINCT cr.chatroom_id) "
        + "FROM chatroom AS cr "
        + "JOIN chatroom_user_mapping AS cum ON cr.chatroom_id = cum.chatroom_id "
        + "WHERE 1=1 ";

    Map<String, Object> map = new HashMap<>();

    // Filtering e.g. status, search
    sql = addFilteringSql(sql, map, listQueryParametersDto);

    Integer total = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class);

    return total;
  }

  @Override
  public boolean isChatroomUserMappingExists(String chatroomId, String userId) {
    String sql = "SELECT COUNT(*) FROM chatroom_user_mapping WHERE chatroom_id = :chatroomId AND user_id = :userId";

    Map<String, Object> map = new HashMap<>();
    map.put("chatroomId", chatroomId);
    map.put("userId", userId);

    boolean isExistsChatroomUserMapping = namedParameterJdbcTemplate.queryForObject(sql, map, Integer.class) > 0;

    return isExistsChatroomUserMapping;
  }



  private String addFilteringSql(String sql, Map<String, Object> map, ListQueryParametersDto listQueryParametersDto) {

    if (listQueryParametersDto.getUserId() != null) {
      sql = sql + " AND cum.user_id = :userId ";
      map.put("userId", listQueryParametersDto.getUserId());
    }



    if (listQueryParametersDto.getSearch() != null) {
      sql = sql + " AND cr.chatroom_id IN ("
          + "  SELECT chatroom_id "
          + "  FROM chatroom_user_mapping AS cum2 "
          + "  JOIN user AS u2 ON cum2.user_id = u2.user_id "
          + "  WHERE cum2.user_id != :userId AND (u2.first_name LIKE :search OR u2.last_name LIKE :search)"
          + ") ";
      map.put("search", "%" + listQueryParametersDto.getSearch() + "%");
    }


    return sql;
  }
}
