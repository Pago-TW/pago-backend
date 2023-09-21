package tw.pago.pagobackend.service.impl;

import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.TextStyle;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;
import tw.pago.pagobackend.dao.TransactionDao;
import tw.pago.pagobackend.dto.TransactionTabViewDto;
import tw.pago.pagobackend.dto.ValidatePhoneRequestDto;
import tw.pago.pagobackend.exception.BadRequestException;
import tw.pago.pagobackend.exception.UnprocessableEntityException;
import tw.pago.pagobackend.model.Otp;
import tw.pago.pagobackend.model.PendingWithdrawal;
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
    public Map<Integer, List<TransactionTabViewDto>> getTransactionTabViewByUserId(String userId) {

        Map<Integer, List<TransactionTabViewDto>> responseMap = new TreeMap<>(Collections.reverseOrder());
        List<String> transactionDistinctYearMonth = transactionDao.getTransactionDistinctYearMonthByUserId(userId);

        for (String ym : transactionDistinctYearMonth) {
            // 解析年和月
            int year = Integer.parseInt(ym.substring(0, 4));
            int month = Integer.parseInt(ym.substring(5, 7));

            // 建立 ZonedDateTime 物件
            ZonedDateTime startDate = ZonedDateTime.of(year, month, 1, 0, 0, 0, 0, ZoneId.of("UTC"));
            ZonedDateTime endDate = startDate.with(TemporalAdjusters.lastDayOfMonth())
                .withHour(23)
                .withMinute(59)
                .withSecond(59)
                .withNano(999999999);

            // 檢查是否需要新建 List
            if (!responseMap.containsKey(year)) {
                responseMap.put(year, new ArrayList<>());
            }

            // 建立新的 TransactionTabViewDto
            TransactionTabViewDto newTabView = new TransactionTabViewDto(
                startDate,
                endDate,
                Month.of(month).getDisplayName(TextStyle.FULL, Locale.forLanguageTag("zh-TW"))
            );

            responseMap.get(year).add(newTabView);
        }

        return responseMap;
    }


    @Override
    public Otp requestWithdraw(Integer withdrawalAmount) {
        User currentUser = currentUserInfoProvider.getCurrentLoginUser();
        String userId = currentUser.getUserId();

        PendingWithdrawal pendingWithdrawal = transactionDao.getPendingWithdrawalByUserId(userId);
        if (pendingWithdrawal != null) {
            throw new BadRequestException("There is a pending withdrawal");
        }

        Integer currentBalance = transactionDao.getWalletBalance(userId);
        if (currentBalance + TRANSACTION_FEE < withdrawalAmount) {
            throw new UnprocessableEntityException("Insufficient balance");
        }

        String phone = currentUser.getPhone();
        Otp otp = otpService.requestOtp(phone);
        transactionDao.requestWithdraw(otp.getOtpId(), userId, withdrawalAmount);

        return otp;
    }

    @Override
    public boolean validateWithdraw(String otpCode) {
        User currentUser = currentUserInfoProvider.getCurrentLoginUser();
        String userId = currentUser.getUserId();
        String phone = currentUser.getPhone();
        ValidatePhoneRequestDto validatePhoneRequestDto = new ValidatePhoneRequestDto();
        validatePhoneRequestDto.setPhone(phone);
        validatePhoneRequestDto.setOtpCode(otpCode);
        boolean isValid = otpService.validateOtp(validatePhoneRequestDto);

        if (!isValid) {
            throw new BadRequestException("Invalid OTP or OTP expired");
        }

        PendingWithdrawal pendingWithdrawal = transactionDao.getPendingWithdrawalByUserId(userId);

        if (!pendingWithdrawal.getUserId().equals(userId)) {
            throw new BadRequestException("Invalid user id");
        }
        transactionDao.deletePendingWithdrawalById(pendingWithdrawal.getPendingWithdrawalId());

        Integer withdrawalAmount = -pendingWithdrawal.getWithdrawalAmount();

        transactionDao.withdraw(userId, withdrawalAmount);
        transactionDao.applyTransactionFee(userId, -TRANSACTION_FEE);

        return true;
    }
}
