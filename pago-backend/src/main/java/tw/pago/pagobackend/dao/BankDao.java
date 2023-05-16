package tw.pago.pagobackend.dao;

import java.util.List;
import tw.pago.pagobackend.model.Bank;

public interface BankDao {

  List<Bank> getBankList();

}
