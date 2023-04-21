package tw.pago.pagobackend.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import tw.pago.pagobackend.model.Chatroom;

public class ChatroomRowMapper implements RowMapper<Chatroom> {

  @Override
  public Chatroom mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    Chatroom chatroom = new Chatroom();
    chatroom.setChatroomId(resultSet.getString("chatroom_id"));
    chatroom.setCreateDate(resultSet.getTimestamp("create_date").toLocalDateTime());
    chatroom.setUpdateDate(resultSet.getTimestamp("update_date").toLocalDateTime());

    return chatroom;
  }
}
