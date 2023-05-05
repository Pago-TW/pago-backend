package tw.pago.pagobackend.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import tw.pago.pagobackend.constant.ReviewTypeEnum;
import tw.pago.pagobackend.model.Review;

public class ReviewRowMapper implements RowMapper<Review> {

  @Override
  public Review mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    Review review = Review.builder()
        .reviewId(resultSet.getString("review_Id"))
        .orderId(resultSet.getString("order_id"))
        .creatorId(resultSet.getString("creator_id"))
        .targetId(resultSet.getString("target_id"))
        .content(resultSet.getString("content"))
        .rating(resultSet.getInt("rating"))
        .createDate(resultSet.getTimestamp("create_date"))
        .reviewType(ReviewTypeEnum.valueOf(resultSet.getString("review_type")))
        .updateDate(resultSet.getTimestamp("update_date"))
        .build();

    return review;
  }
}
