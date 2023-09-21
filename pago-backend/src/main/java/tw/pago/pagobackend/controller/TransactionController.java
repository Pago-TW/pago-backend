package tw.pago.pagobackend.controller;

import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import lombok.AllArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import tw.pago.pagobackend.dto.ListQueryParametersDto;
import tw.pago.pagobackend.dto.OtpValidationDto;
import tw.pago.pagobackend.dto.TransactionRecordListResponseDto;
import tw.pago.pagobackend.dto.TransactionTabViewDto;
import tw.pago.pagobackend.dto.TransactionWithdrawRequestDto;
import tw.pago.pagobackend.model.Otp;
import tw.pago.pagobackend.model.TransactionRecord;
import tw.pago.pagobackend.service.TransactionService;
import tw.pago.pagobackend.util.CurrentUserInfoProvider;

@RestController
@AllArgsConstructor
public class TransactionController {

    private final CurrentUserInfoProvider currentUserInfoProvider;
    private final TransactionService transactionService;

    @GetMapping("/wallet/balance")
    public ResponseEntity<?> getWalletBalance() {
        String userId = currentUserInfoProvider.getCurrentLoginUserId();
        Integer balance = transactionService.getWalletBalance(userId);
        
        return ResponseEntity.status(HttpStatus.OK).body(balance);
    }

    @GetMapping("/wallet/transactions")
    public ResponseEntity<List<TransactionRecordListResponseDto>> getTransactionList(
        @RequestParam (required = false) @DateTimeFormat(iso = ISO.DATE_TIME) ZonedDateTime startDate ,
        @RequestParam (required = false) @DateTimeFormat(iso = ISO.DATE_TIME) ZonedDateTime endDate
    ) {
        String userId = currentUserInfoProvider.getCurrentLoginUserId();
        ListQueryParametersDto listQueryParametersDto = ListQueryParametersDto.builder()
            .userId(userId)
            .startDate(startDate)
            .endDate(endDate)
            .build();
        List<TransactionRecord> transactionRecordList = transactionService.getTransactionRecordList(listQueryParametersDto);
        List<TransactionRecordListResponseDto> response = transactionService.getTransactionRecordResponseDtoListByTransactionRecordList(transactionRecordList);
        
        return ResponseEntity.status(HttpStatus.OK).body(response);
    }

    @GetMapping("/wallet/transactions/{transactionId}")
    public ResponseEntity<?> getTransactionById(@PathVariable String transactionId) {
        String userId = currentUserInfoProvider.getCurrentLoginUserId();
        TransactionRecord transactionRecord = transactionService.getTransactionById(userId, transactionId);
        
        return ResponseEntity.status(HttpStatus.OK).body(transactionRecord);
    }

    @GetMapping("/wallet/transactions/tab-view")
    public ResponseEntity<Map<Integer, List<TransactionTabViewDto>>> getTransactionTabView() {
        String userId = currentUserInfoProvider.getCurrentLoginUserId();


        return ResponseEntity.status(HttpStatus.OK).body(transactionService.getTransactionTabViewByUserId(userId));
    }

    @PostMapping("/wallet/withdraw")
    public ResponseEntity<?> requestWithdraw(@RequestBody TransactionWithdrawRequestDto transactionWithdrawRequestDto) {
        Integer withdrawalAmount = transactionWithdrawRequestDto.getWithdrawalAmount();
        Otp otp = transactionService.requestWithdraw(withdrawalAmount);

        return ResponseEntity.status(HttpStatus.CREATED).body(otp);
    }

    @PostMapping("/wallet/withdraw/validate")
    public ResponseEntity<?> validateWithdraw(@RequestBody OtpValidationDto otpValidationDto) {
        String otpCode = otpValidationDto.getOtpCode();
        boolean isValid = transactionService.validateWithdraw(otpCode);
        return ResponseEntity.status(HttpStatus.OK).body(isValid);
    }



}
