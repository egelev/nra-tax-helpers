package com.egelev.nra;

import com.egelev.nra.model.InvestmentSecurity;
import com.egelev.nra.model.SecurityHolding;
import com.egelev.nra.model.SellOrder;
import com.egelev.nra.model.Transaction;
import java.math.BigDecimal;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public interface PortfolioService {

  SecurityHolding addTransaction(Transaction transaction);
  Optional<SecurityHolding> getSecurity(InvestmentSecurity investmentSecurity);
  Set<SecurityHolding> getSecurityHoldings();
  Set<SellOrder> getSellOrders();
  Map<InvestmentSecurity, BigDecimal> getDividends();

}
