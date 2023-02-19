package tw.pago.pagobackend.constant;

public enum OrderStatusEnum {
  REQUESTED,
  TO_BE_PURCHASED,
  TO_BE_DELIVERED,
  CONSUMER_HAS_CONFIRMED,
  SHOPPER_HAS_CONFIRMED,
  FINISHED,
  CANCELED
}
