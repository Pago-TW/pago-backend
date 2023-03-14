// package tw.pago.pagobackend.rowmapper;

// import java.sql.ResultSet;
// import java.sql.SQLException;
// import org.springframework.jdbc.core.RowMapper;
// import tw.pago.pagobackend.constant.MatchingStatusEnum;
// import tw.pago.pagobackend.model.Matching;

// public class MatchingRowMapper implements RowMapper<Matching> {

//   @Override
//   public Matching mapRow(ResultSet resultSet, int rowNum) throws SQLException {
//     Matching matching = new Matching();
//     matching.setMatchingId(resultSet.getString("matching_id"));
//     matching.setBidId(resultSet.getString("bid_id"));
//     matching.setCreateDate(resultSet.getTimestamp("create_date"));
//     matching.setMatchingStatus(MatchingStatusEnum.valueOf(resultSet.getString("matching_status")));

//     return matching;
//   }
// }
