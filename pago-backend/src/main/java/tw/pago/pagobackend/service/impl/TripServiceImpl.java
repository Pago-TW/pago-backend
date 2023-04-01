package tw.pago.pagobackend.service.impl;

import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import tw.pago.pagobackend.assembler.TripAssembler;
import tw.pago.pagobackend.constant.BidStatusEnum;
import tw.pago.pagobackend.constant.OrderStatusEnum;
import tw.pago.pagobackend.constant.TripStatusEnum;
import tw.pago.pagobackend.dao.BidDao;
import tw.pago.pagobackend.dao.OrderDao;
import tw.pago.pagobackend.dao.TripDao;
import tw.pago.pagobackend.dto.CreateTripRequestDto;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.TripDashboardDto;
import tw.pago.pagobackend.dto.TripResponseDto;
import tw.pago.pagobackend.dto.UpdateTripRequestDto;
import tw.pago.pagobackend.model.Bid;
import tw.pago.pagobackend.model.Order;
import tw.pago.pagobackend.model.Trip;
import tw.pago.pagobackend.service.TripService;
import tw.pago.pagobackend.util.UuidGenerator;

@Component
public class TripServiceImpl implements TripService {

    @Autowired
    private TripAssembler tripAssembler;
    @Autowired
    private TripDao tripDao;
    @Autowired
    private UuidGenerator uuidGenerator;
    @Autowired
    private BidDao bidDao;
    @Autowired
    private OrderDao orderDao;

    @Override
    public Trip getTripById(String tripId) {
        return tripDao.getTripById(tripId);
    }

    @Override
    public TripResponseDto getTripResponseDtoByTrip(Trip trip) {
        // Convert model to DTO
        TripResponseDto tripResponseDto = tripAssembler.assemble(trip);

        setTripStatusBasedOnDates(tripResponseDto);


        TripDashboardDto tripDashboardDto = getTripDashBoardDtoByTrip(trip);
        tripResponseDto.setDashboard(tripDashboardDto);





        return tripResponseDto;
    }

    //    @Override
//    public List<Trip> findAll() throws SQLException {
//        return tripDAO.findAll();
//    }

    @Override
    public String createTrip(String userId, CreateTripRequestDto createTripRequestDto) {
        String tripUuid = uuidGenerator.getUuid();
        createTripRequestDto.setTripId(tripUuid);
        return tripDao.createTrip(userId, createTripRequestDto);
    }

    @Override
    public void updateTrip(Trip trip, UpdateTripRequestDto updateTripRequestDto) {
        tripDao.updateTrip(trip, updateTripRequestDto);
    }

    @Override
    public void deleteTripById(String tripId) throws SQLException {
        tripDao.delete(tripId);
    }

    @Override
    public List<Trip> getTripList(ListQueryParametersDto listQueryParametersDto) {

        List<Trip> tripList = tripDao.getTripList(listQueryParametersDto);
        return tripList;
    }


    @Override
    public Integer countTrip(ListQueryParametersDto listQueryParametersDto) {
        Integer total = tripDao.countTrip(listQueryParametersDto);

        return total;
    }

    private void setTripStatusBasedOnDates(TripResponseDto tripResponseDto) {
        LocalDate currentDate = LocalDate.now();

        // Change arrivalDate data type from `Date` to `LocalDate`
        Instant arrivalDateInstant = tripResponseDto.getArrivalDate().toInstant();
        LocalDate arrivalDate = arrivalDateInstant.atZone(ZoneOffset.UTC).toLocalDate();

        // Set TripStatus by comparing with currentDate
        int maxDaysOfTrip = 7;
        LocalDate endDateOfTrip = arrivalDate.plusDays(maxDaysOfTrip);

        if (currentDate.isBefore(arrivalDate)) {
            tripResponseDto.setTripStatus(TripStatusEnum.UPCOMING);
        } else if (currentDate.isAfter(arrivalDate) && currentDate.isBefore(endDateOfTrip)) {
            tripResponseDto.setTripStatus(TripStatusEnum.ONGOING);
        } else {
            tripResponseDto.setTripStatus(TripStatusEnum.PAST);
        }
    }


    private TripDashboardDto getTripDashBoardDtoByTrip(Trip trip) {
        // Initialize counts for each status category
        int totalRequested = 0;
        int totalToBePurchased = 0;
        int totalToBeDelivered = 0;

        // Create list query parameters with the trip ID and a large size limit
        ListQueryParametersDto listQueryParametersDto = ListQueryParametersDto.builder()
            .orderBy("create_date")
            .sort("DESC")
            .startIndex(0)
            .size(999)
            .tripId(trip.getTripId())
            .build();

        // Retrieve the list of bids for the given trip
        List<Bid> bidListForTrip = bidDao.getBidList(listQueryParametersDto);

        // Initialize lists for chosen bids and corresponding orders
        List<Bid> isChosenBidList = new ArrayList<>();
        List<Order> orderListForChosenBids = new ArrayList<>();

        // Iterate through the bid list to update status counts and store chosen bids
        for (Bid bid : bidListForTrip) {
            if (bid.getBidStatus().equals(BidStatusEnum.NOT_CHOSEN)) {
                totalRequested += 1;
            } else if (bid.getBidStatus().equals(BidStatusEnum.IS_CHOSEN)) {
                isChosenBidList.add(bid);
            }
        }

        // Iterate through the chosen bid list and retrieve the corresponding orders
        for (Bid bid : isChosenBidList) {
            Order order = orderDao.getOrderById(bid.getOrderId());
            orderListForChosenBids.add(order);
        }

        // Iterate through the chosen order list to update status counts
        for (Order order : orderListForChosenBids) {
            if (order.getOrderStatus().equals(OrderStatusEnum.TO_BE_PURCHASED)) {
                totalToBePurchased += 1;
            } else if (order.getOrderStatus().equals(OrderStatusEnum.TO_BE_DELIVERED)) {
                totalToBeDelivered += 1;
            }
        }

        // Build the TripDashboardDto object with the computed status counts
        TripDashboardDto tripDashboardDto = new TripDashboardDto();
        tripDashboardDto.setRequested(totalRequested);
        tripDashboardDto.setToBePurchased(totalToBePurchased);
        tripDashboardDto.setToBeDelivered(totalToBeDelivered);

        // Return the TripDashboardDto object
        return tripDashboardDto;
    }

}
