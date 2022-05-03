package com.egelev.nra.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record OpenPosition(
    BigDecimal quantity,
    BigDecimal averageSinglePrice,
    ZonedDateTime latestBuyTimestamp,
    InvestmentSecurity investmentSecurity
) {

  public OpenPosition(OpenPosition toCopy) {
    this(
        toCopy.quantity,
        toCopy.averageSinglePrice,
        toCopy.latestBuyTimestamp,
        toCopy.investmentSecurity
    );
  }
}
