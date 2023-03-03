package tw.pago.pagobackend.constant;

public enum OrderStatusEnum {
  // 在信件說明好雙方權益
  REQUESTED, // 1 待確認
  TO_BE_PURCHASED, // 2 待購買
  TO_BE_DELIVERED, // 3 待面交
  DELIVERED, // 代購者按  4 已送達
  FINISHED,  // 委託者按  5 已完成
  CANCELED
}
