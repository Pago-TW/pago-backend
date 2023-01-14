package tw.pago.pagobackend.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tw.pago.pagobackend.dao.BidDao;
import tw.pago.pagobackend.dto.CreateBidRequestDto;
import tw.pago.pagobackend.service.BidService;


@Component
public class BidServiceImpl implements BidService {

  @Autowired
  private BidDao bidDao;

  @Override
  public Integer createBid(CreateBidRequestDto createBidRequestDto) {

    Integer bidId = bidDao.createBid(createBidRequestDto);

    return bidId;
  }
}
