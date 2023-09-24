package tw.pago.pagobackend.service.impl;

import java.util.List;

import org.springframework.stereotype.Component;

import lombok.AllArgsConstructor;
import tw.pago.pagobackend.dao.TransactionDao;
import tw.pago.pagobackend.dto.TransactionWithdrawRequestDto;
import tw.pago.pagobackend.dto.ValidatePhoneRequestDto;
import tw.pago.pagobackend.exception.BadRequestException;
import tw.pago.pagobackend.exception.UnprocessableEntityException;
import tw.pago.pagobackend.model.TransactionRecord;
import tw.pago.pagobackend.model.User;
import tw.pago.pagobackend.service.OtpService;
import tw.pago.pagobackend.service.TransactionService;
import tw.pago.pagobackend.util.CurrentUserInfoProvider;

@Component
@AllArgsConstructor
public class TransactionServiceImpl implements TransactionService {
    
    private final TransactionDao transactionDao;

    private final OtpService otpService;

    private final CurrentUserInfoProvider currentUserInfoProvider;

    private static final Integer TRANSACTION_FEE = 15;

    @Override
    public Integer getWalletBalance(String userId) {
        return transactionDao.getWalletBalance(userId);
    }

    @Override
    public List<TransactionRecord> getTransactionList(String userId) {
        return transactionDao.getTransactionList(userId);
    }

    @Override
    public TransactionRecord getTransactionById(String userId, String transactionId) {
        return transactionDao.getTransactionById(userId, transactionId);
    }

    @Override
    public void requestWithdraw(TransactionWithdrawRequestDto transactionWithdrawRequestDto) {
        User currentUser = currentUserInfoProvider.getCurrentLoginUser();
        Integer withdrawalAmount = transactionWithdrawRequestDto.getWithdrawalAmount();
        String otpCode = transactionWithdrawRequestDto.getOtpCode();
        String userId = currentUser.getUserId();
        String phone = currentUser.getPhone();
        ValidatePhoneRequestDto validatePhoneRequestDto = new ValidatePhoneRequestDto();
        validatePhoneRequestDto.setPhone(phone);
        validatePhoneRequestDto.setOtpCode(otpCode);

        boolean isValid = otpService.validateOtp(validatePhoneRequestDto);

        if (!isValid) {
            throw new BadRequestException("Invalid OTP or OTP expired");
        }

        Integer currentBalance = transactionDao.getWalletBalance(userId);
        if (currentBalance + TRANSACTION_FEE < withdrawalAmount) {
            throw new UnprocessableEntityException("Insufficient balance");
        }

        transactionDao.withdraw(userId, -withdrawalAmount);
        transactionDao.applyTransactionFee(userId, -TRANSACTION_FEE);

    }
}
