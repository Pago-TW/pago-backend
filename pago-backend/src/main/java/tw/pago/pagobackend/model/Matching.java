package tw.pago.pagobackend.model;

import java.util.Date;
import tw.pago.pagobackend.constant.MatchingStatusEnum;

public class Matching {

  String matchingId;
  String bidId;
  Date createDate;
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

  public Date getCreateDate() {
    return createDate;
  }

  public void setCreateDate(Date createDate) {
    this.createDate = createDate;
  }

  public MatchingStatusEnum getMatchingStatus() {
    return matchingStatus;
  }

  public void setMatchingStatus(MatchingStatusEnum matchingStatus) {
    this.matchingStatus = matchingStatus;
  }
}
