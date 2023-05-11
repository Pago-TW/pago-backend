package tw.pago.pagobackend.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.ZoneId;
import org.springframework.jdbc.core.RowMapper;
import tw.pago.pagobackend.model.ChatroomUserMapping;

public class ChatroomUserMappingRowMapper implements RowMapper<ChatroomUserMapping> {

  @Override
  public ChatroomUserMapping mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    ChatroomUserMapping chatroomUserMapping = new ChatroomUserMapping();
    chatroomUserMapping.setChatroomUserMappingId(resultSet.getString("chatroom_user_mapping_id"));
    chatroomUserMapping.setChatroomId(resultSet.getString("chatroom_id"));
    chatroomUserMapping.setUserId(resultSet.getString("user_id"));
    chatroomUserMapping.setLastReadMessageId(resultSet.getString("last_read_message_id"));
    chatroomUserMapping.setCreateDate(resultSet.getTimestamp("create_date").toInstant().atZone(
        ZoneId.of("UTC")));
    chatroomUserMapping.setUpdateDate(resultSet.getTimestamp("update_date").toInstant().atZone(ZoneId.of("UTC")));


    return chatroomUserMapping;
  }
}
