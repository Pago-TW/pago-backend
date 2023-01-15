package tw.pago.pagobackend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tw.pago.pagobackend.dao.BidDao;
import tw.pago.pagobackend.dto.CreateBidRequestDto;
import tw.pago.pagobackend.model.Bid;
import tw.pago.pagobackend.service.BidService;
import tw.pago.pagobackend.util.UuidGenerator;


@Component
public class BidServiceImpl implements BidService {

  @Autowired
  private BidDao bidDao;
  @Autowired
  private UuidGenerator uuidGenerator;

  @Override
  public Bid createBid(CreateBidRequestDto createBidRequestDto) {

    // Init UUID & Set UUID
    String uuid = uuidGenerator.getUuid();
    createBidRequestDto.setBidId(uuid);

    // Create bid
    bidDao.createBid(createBidRequestDto);
    Bid bid = bidDao.getBidById(uuid);

    return bid;
  }

  @Override
  public Bid getBidById(String bidId) {
    Bid bid = bidDao.getBidById(bidId);
    return bid;

  }

  @Override
  public void deleteBidById(Integer bidId) {
    bidDao.deleteBidById(bidId);
  }
}
