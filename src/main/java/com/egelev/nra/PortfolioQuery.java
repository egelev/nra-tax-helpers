package com.egelev.nra;

import com.egelev.nra.model.Currency;
import java.time.ZonedDateTime;
import java.util.Collection;

public interface PortfolioQuery {

  Portfolio execute();

  PortfolioQuery filterByTickerSymbol(Collection<String> allowedTickerSymbols);
  PortfolioQuery filterByExchange(Collection<String> allowedExchanges);
  PortfolioQuery filterByCurrency(Collection<Currency> allowedCurrencies);
  PortfolioQuery filterByDate(ZonedDateTime after, ZonedDateTime before);

}
