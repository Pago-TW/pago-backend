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
    public Trip get(Integer trip_id) throws SQLException {
        Trip trip = null;
        String sql = "SELECT * FROM trip WHERE trip_id = ?";
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);) {
            stmt.setInt(1, trip_id);
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
    public List<Trip> getList() throws SQLException {
        List<Trip> trips = new ArrayList<Trip>();
        String sql = "SELECT * FROM trip";
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
        Integer trip_id;
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
            trip_id = rs.getInt(1);
        } catch (SQLException e) {
            throw e;
        }
        return trip_id;
    }

    @Override
    public void update(Integer trip_id, Trip trip_request) throws SQLException {
        String sql = "UPDATE trip SET traveler_id=?, from_location=?, to_location=?, arrival_date=?, profit=? WHERE trip_id=?";
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);) {
            stmt.setInt(1, trip_request.getTravelerId());
            stmt.setString(2, trip_request.getFromLocation());
            stmt.setString(3, trip_request.getToLocation());
            stmt.setString(4, trip_request.getArrivalDate());
            stmt.setDouble(5, trip_request.getProfit());
            stmt.setInt(6, trip_id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }
    }

    public void delete(Integer trip_id) throws SQLException {
        String sql = "DELETE FROM trip WHERE trip_id=?";
        try (
                Connection conn = dataSource.getConnection();
                PreparedStatement stmt = conn.prepareStatement(sql);) {
            stmt.setInt(1, trip_id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw e;
        }
    }
}
