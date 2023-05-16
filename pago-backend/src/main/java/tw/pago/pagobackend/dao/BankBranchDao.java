package tw.pago.pagobackend.dao;

import java.util.List;
import tw.pago.pagobackend.model.BankBranch;

public interface BankBranchDao {

  List<BankBranch> getBankBranchListByAdministrativeDivisionAndBankCode(String administrativeDivision, String bankCode);

}
