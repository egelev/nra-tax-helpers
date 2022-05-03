package com.egelev.nra.api.rest.resources;

import com.egelev.nra.model.Currency;

public record InvestmentSecurityResource(
    Currency currency,
    String tickerSymbol,
    String isin,
    String name,
    String exchange
) {

}
