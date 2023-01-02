package tw.pago.pagobackend.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import tw.pago.pagobackend.dao.TripDAO;
import tw.pago.pagobackend.entity.Trip;

@Component
public class TripDAOImpl implements TripDAO {
    @Autowired
    private DataSource dataSource;

    @Override
    public Trip getById(Integer tripId) throws SQLException {
        Trip trip = null;
        String sql = "SELECT trip_id, traveler_id, from_location, to_location, arrival_date, profit FROM trip WHERE trip_id = ?";
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);) {
            stmt.setInt(1, tripId);
            try (ResultSet rs = stmt.executeQuery();) {
                if (rs.next()) {
                    trip = new Trip(0, 0, "", "", "", 0);
                    trip.setTripId(rs.getInt("trip_id"));
                    trip.setTravelerId(rs.getInt("traveler_id"));
                    trip.setFromLocation(rs.getString("from_location"));
                    trip.setToLocation(rs.getString("to_location"));
                    trip.setArrivalDate(rs.getString("arrival_date"));
                    trip.setProfit(rs.getDouble("profit"));
                }
            }
        } catch (SQLException e) {
            throw e;
        }
        return trip;
    }

    @Override
    public List<Trip> findAll() throws SQLException {
        List<Trip> trips = new ArrayList<Trip>();
        String sql = "SELECT trip_id, traveler_id, from_location, to_location, arrival_date, profit FROM trip";
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery();) {
            while (rs.next()) {
                Trip trip = new Trip(0, 0, "", "", "", 0);
                trip.setTripId(rs.getInt("trip_id"));
                trip.setTravelerId(rs.getInt("traveler_id"));
                trip.setFromLocation(rs.getString("from_location"));
                trip.setToLocation(rs.getString("to_location"));
                trip.setArrivalDate(rs.getString("arrival_date"));
                trip.setProfit(rs.getDouble("profit"));
                trips.add(trip);
            }
        } catch (SQLException e) {
            throw e;
        }
        return trips;
    }

    @Override
    public Integer insert(Trip trip) throws SQLException {
        String sql = "INSERT INTO trip (traveler_id, from_location, to_location, arrival_date, profit) values(?, ?, ?, ?, ?)";
        Integer tripId;
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);) {
            stmt.setInt(1, trip.getTravelerId());
            stmt.setString(2, trip.getFromLocation());
            stmt.setString(3, trip.getToLocation());
            stmt.setString(4, trip.getArrivalDate());
            stmt.setDouble(5, trip.getProfit());
            stmt.executeUpdate();
            ResultSet rs = stmt.getGeneratedKeys();
            rs.next();
            tripId = rs.getInt(1);
        } catch (SQLException e) {
            throw e;
        }
        return tripId;
    }

    @Override
    public void update(Integer tripId, Trip tripRequest) throws SQLException {
        String sql = "UPDATE trip SET traveler_id=?, from_location=?, to_location=?, arrival_date=?, profit=? WHERE trip_id=?";
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);) {
            stmt.setInt(1, tripRequest.getTravelerId());
            stmt.setString(2, tripRequest.getFromLocation());
            stmt.setString(3, tripRequest.getToLocation());
            stmt.setString(4, tripRequest.getArrivalDate());
            stmt.setDouble(5, tripRequest.getProfit());
            stmt.setInt(6, tripId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }
    }

    public void delete(Integer tripId) throws SQLException {
        String sql = "DELETE FROM trip WHERE trip_id=?";
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);) {
            stmt.setInt(1, tripId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }
    }
}
