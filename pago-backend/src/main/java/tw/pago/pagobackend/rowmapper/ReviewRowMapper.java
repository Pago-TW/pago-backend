package tw.pago.pagobackend.rowmapper;

import java.sql.ResultSet;
import java.sql.SQLException;
import org.springframework.jdbc.core.RowMapper;
import tw.pago.pagobackend.constant.ReviewTypeEnum;
import tw.pago.pagobackend.model.Review;

public class ReviewRowMapper implements RowMapper<Review> {

  @Override
  public Review mapRow(ResultSet resultSet, int rowNum) throws SQLException {
    Review review = new Review();
    review.setReviewId(resultSet.getString("review_Id"));
    review.setOrderId(resultSet.getString("order_id"));
    review.setTravelerId(resultSet.getString("traveler_id"));
    review.setShopperId(resultSet.getString("shopper_id"));
    review.setContent(resultSet.getString("content"));
    review.setReviewImagePath(resultSet.getString("review_image_path"));
    review.setRating(resultSet.getInt("rating"));
    review.setCreateDate(resultSet.getTimestamp("create_date"));
    review.setReviewType(ReviewTypeEnum.valueOf(resultSet.getString("review_type")));
    review.setUpdateDate(resultSet.getTimestamp("update_date"));



    return review;
  }
}
