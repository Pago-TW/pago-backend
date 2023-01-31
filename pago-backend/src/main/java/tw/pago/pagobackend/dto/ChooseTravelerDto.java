package tw.pago.pagobackend.dto;

import tw.pago.pagobackend.constant.MatchingStatusEnum;

public class ChooseTravelerDto {


  String matchingId;
  String bidId;
  MatchingStatusEnum matchingStatus;

  public String getMatchingId() {
    return matchingId;
  }

  public void setMatchingId(String matchingId) {
    this.matchingId = matchingId;
  }

  public String getBidId() {
    return bidId;
  }

  public void setBidId(String bidId) {
    this.bidId = bidId;
  }

  public MatchingStatusEnum getMatchingStatus() {
    return matchingStatus;
  }

  public void setMatchingStatus(MatchingStatusEnum matchingStatus) {
    this.matchingStatus = matchingStatus;
  }
}
