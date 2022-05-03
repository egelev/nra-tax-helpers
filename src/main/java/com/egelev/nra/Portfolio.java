package com.egelev.nra;

import com.egelev.nra.model.Dividend;
import com.egelev.nra.model.InvestmentSecurity;
import com.egelev.nra.model.OpenPosition;
import com.egelev.nra.model.SellOrder;
import com.egelev.nra.model.Transaction;
import java.util.List;
import java.util.Optional;
import java.util.Set;

public interface Portfolio {

  OpenPosition addTransaction(Transaction transaction);
  Optional<OpenPosition> getOpenPosition(InvestmentSecurity investmentSecurity);
  Set<OpenPosition> getOpenPositions();
  Set<SellOrder> getSellOrders();
  List<Dividend> getDividends();

}
