package tw.pago.pagobackend.dao.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;
import tw.pago.pagobackend.dao.ReviewDao;
import tw.pago.pagobackend.dto.CreateReviewRequestDto;
import tw.pago.pagobackend.model.Review;
import tw.pago.pagobackend.rowmapper.ReviewRowMapper;

@Component
public class ReviewDaoImpl implements ReviewDao {

  @Autowired
  NamedParameterJdbcTemplate namedParameterJdbcTemplate;


  @Override
  public void createReview(CreateReviewRequestDto createReviewRequestDto) {
    String sql = "INSERT INTO review (review_id, order_id, creator_id, target_id, content, "
        + "rating, create_date, review_type, update_date) "
        + "VALUES (:reviewId, :orderId, :creatorId, :targetId, :content, "
        + ":rating, :createDate, :reviewType, :updateDate)";

    Map<String, Object> map = new HashMap<>();
    map.put("reviewId", createReviewRequestDto.getReviewId());
    map.put("orderId", createReviewRequestDto.getOrderId());
    map.put("creatorId", createReviewRequestDto.getCreatorId());
    map.put("targetId", createReviewRequestDto.getTargetId());
    map.put("content", createReviewRequestDto.getContent());
    map.put("rating", createReviewRequestDto.getRating());

    Date now = new Date();
    map.put("createDate", now);
    map.put("reviewType", createReviewRequestDto.getReviewType().name());
    map.put("updateDate", now);

    namedParameterJdbcTemplate.update(sql, map);
  }

  @Override
  public Review getReviewById(String reviewId) {
    String sql = "SELECT review_id, order_id, creator_id, target_id, content, "
        + "rating, create_date, review_type, update_date "
        + "FROM review "
        + "WHERE review_id = :reviewId";

    Map<String, Object> map = new HashMap<>();
    map.put("reviewId", reviewId);

    List<Review> reviewList = namedParameterJdbcTemplate.query(sql, map, new ReviewRowMapper());

    if (reviewList.size() > 0) {
      return reviewList.get(0);
    } else {
      return null;
    }

  }
}
