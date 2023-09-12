package tw.pago.pagobackend.rowmapper;

import org.springframework.jdbc.core.RowMapper;

import tw.pago.pagobackend.model.PendingWithdrawal;

public class PendingWithdrawalRowMapper implements RowMapper<PendingWithdrawal> {
    
    @Override
    public PendingWithdrawal mapRow(java.sql.ResultSet rs, int rowNum) throws java.sql.SQLException {
        return PendingWithdrawal.builder()
            .pendingWithdrawalId(rs.getString("pending_withdrawal_id"))
            .userId(rs.getString("user_id"))
            .withdrawalAmount(rs.getInt("amount"))
            .build();
    }
}
