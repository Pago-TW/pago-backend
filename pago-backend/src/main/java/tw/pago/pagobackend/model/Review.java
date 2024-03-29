package tw.pago.pagobackend.model;

import java.net.URL;
import java.util.Date;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import tw.pago.pagobackend.constant.ReviewTypeEnum;


@Getter
@Setter
@Builder
public class Review {
  private String reviewId;
  private String orderId;
  private String creatorId;
  private String targetId;
  private String content;
  private Integer rating;
  private Date createDate;
  private ReviewTypeEnum reviewType;
  private Date updateDate;
  private List<URL> fileUrls;
}
