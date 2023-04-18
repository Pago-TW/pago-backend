package tw.pago.pagobackend.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import tw.pago.pagobackend.model.Chatroom;

public class ChatroomRowMapper implements RowMapper<Chatroom> {

  @Override
  public Chatroom mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    Chatroom chatroom = new Chatroom();
    chatroom.setChatRoomId(resultSet.getString("chatroom_id"));
    chatroom.setCreateDate(resultSet.getDate("create_date").toLocalDate());
    chatroom.setUpdateDate(resultSet.getDate("update_date").toLocalDate());

    return chatroom;
  }
}
