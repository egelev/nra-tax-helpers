package com.egelev.nra.api.rest.resources;

import com.egelev.nra.model.Currency;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

public record SellOrderResource(
    ZonedDateTime timestamp,
    InvestmentSecurityResource investmentSecurityResource,
    BigDecimal quantity,
    BigDecimal buyPrice,
    BigDecimal sellPrice,
    BigDecimal totalBuy,
    BigDecimal totalSell,
    Currency localCurrency,
    BigDecimal totalBuyLocalCurrency,
    BigDecimal totalSellLocalCurrency,
    BigDecimal profit,
    BigDecimal loss
) {

}
