package tw.pago.pagobackend.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import javax.swing.tree.TreePath;
import org.springframework.jdbc.core.RowMapper;
import tw.pago.pagobackend.constant.MessageTypeEnum;
import tw.pago.pagobackend.model.Message;

public class MessageRowMapper implements RowMapper<Message> {

  @Override
  public Message mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    Message message = Message.builder()
        .messageId(resultSet.getString("message_id"))
        .chatRoomId(resultSet.getString("chatroom_id"))
        .content(resultSet.getString("content"))
        .senderId(resultSet.getString("sender_id"))
        .messageType(MessageTypeEnum.valueOf(resultSet.getString("message_type")))
        .sendDate(resultSet.getDate("send_date").toLocalDate())
        .build();



    return message;
  }
}
