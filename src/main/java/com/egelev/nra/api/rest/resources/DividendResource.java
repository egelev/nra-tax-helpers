package com.egelev.nra.api.rest.resources;

import com.egelev.nra.model.Currency;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

public record DividendResource(
    InvestmentSecurityResource investmentSecurityResource,
    List<Payment> payments,
    BigDecimal totalIncome,
    BigDecimal totalTax,
    Currency localCurrency,
    List<Payment> paymentsLocalCurrency,
    BigDecimal totalIncomeLocalCurrency,
    BigDecimal totalTaxLocalCurrency,
    BigDecimal remainingTaxTo5percent,
    BigDecimal _05pCredit
) {

  public static final record Payment (
      ZonedDateTime timestamp,
      BigDecimal income,
      BigDecimal tax
  ) {}

}
