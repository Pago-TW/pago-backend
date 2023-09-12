package tw.pago.pagobackend.model;

import java.util.Date;

import lombok.Builder;
import lombok.Data;
import tw.pago.pagobackend.constant.TransactionTypeEnum;

@Data
@Builder
public class TransactionRecord {
    private String transactionId;
    private String userId;
    private Integer transactionAmount;
    private TransactionTypeEnum transactionType;
    private Date transactionDate;
    private String transactionStatus;

    // withdraw
    private String bankAccountId; // bank_account table: get bank_code and accountNumber by id
    // private String bankCode;
    private String accountNumber;
    private String bankName; // bank table: get bank_name by bank_code


    //order_income, order_refund
    private String orderId; // order_main table: get order_item_id and serial_number by id
    // private String orderItemId;
    private String orderSerialNumber;
    private String orderName; // order_item table: get name by order_item_id
    private String cancelReason; // cancellation_record table: get cancel_reason by order_id

}
