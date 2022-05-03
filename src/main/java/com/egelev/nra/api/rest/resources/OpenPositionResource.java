package com.egelev.nra.api.rest.resources;

import com.egelev.nra.model.Currency;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record OpenPositionResource(
    ZonedDateTime latestBuyTimestamp,
    InvestmentSecurityResource investmentSecurity,
    BigDecimal quantity,
    BigDecimal averageSinglePrice,
    BigDecimal total,
    Currency localCurrency,
    BigDecimal totalHoldingsLocalCurrency
) {

}
