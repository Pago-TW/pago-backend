package tw.pago.pagobackend.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ReviewRatingResultDto {
  private double averageRating;
  private int totalReviews;


}
