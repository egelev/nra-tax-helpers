package com.egelev.nra.model;

import java.math.BigDecimal;

public record SecurityHolding(
    BigDecimal quantity,
    BigDecimal averageSinglePrice,
    BigDecimal dividend,
    InvestmentSecurity investmentSecurity
) {

  public SecurityHolding(
      BigDecimal quantity,
      BigDecimal averageSinglePrice,
      InvestmentSecurity investmentSecurity) {
    this(quantity, averageSinglePrice, BigDecimal.ZERO, investmentSecurity);
  }
}
