package tw.pago.pagobackend.dto;

import java.util.Date;
import tw.pago.pagobackend.constant.ReviewTypeEnum;

public class CreateReviewRequestDto {
  private String reviewId;
  private String orderId;
  private String travelerId;
  private String shopperId;
  private String content;
  private String reviewImagePath;
  private Integer rating;
  private ReviewTypeEnum reviewType;


  public CreateReviewRequestDto() {

  }

  public String getReviewId() {
    return reviewId;
  }

  public void setReviewId(String reviewId) {
    this.reviewId = reviewId;
  }

  public String getOrderId() {
    return orderId;
  }

  public void setOrderId(String orderId) {
    this.orderId = orderId;
  }

  public String getTravelerId() {
    return travelerId;
  }

  public void setTravelerId(String travelerId) {
    this.travelerId = travelerId;
  }

  public String getShopperId() {
    return shopperId;
  }

  public void setShopperId(String shopperId) {
    this.shopperId = shopperId;
  }

  public String getContent() {
    return content;
  }

  public void setContent(String content) {
    this.content = content;
  }

  public String getReviewImagePath() {
    return reviewImagePath;
  }

  public void setReviewImagePath(String reviewImagePath) {
    this.reviewImagePath = reviewImagePath;
  }

  public Integer getRating() {
    return rating;
  }

  public void setRating(Integer rating) {
    this.rating = rating;
  }

  public ReviewTypeEnum getReviewType() {
    return reviewType;
  }

  public void setReviewType(ReviewTypeEnum reviewType) {
    this.reviewType = reviewType;
  }
}
