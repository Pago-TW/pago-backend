package tw.pago.pagobackend.service;

import java.util.List;
import java.util.Map;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.TransactionRecordListResponseDto;
import tw.pago.pagobackend.dto.TransactionTabViewDto;
import tw.pago.pagobackend.model.Otp;
import tw.pago.pagobackend.model.TransactionRecord;

public interface TransactionService {
    Integer getWalletBalance(String userId);

    List<TransactionRecord> getTransactionRecordList(ListQueryParametersDto listQueryParametersDto);

    List<TransactionRecordListResponseDto> getTransactionRecordResponseDtoListByTransactionRecordList(List<TransactionRecord> transactionRecordList);

    TransactionRecord getTransactionById(String userId, String transactionId);

    Map<Integer, List<TransactionTabViewDto>> getTransactionTabViewByUserId(String userId);

    Otp requestWithdraw(Integer withdrawalAmount);

    boolean validateWithdraw(String otpCode);
}
