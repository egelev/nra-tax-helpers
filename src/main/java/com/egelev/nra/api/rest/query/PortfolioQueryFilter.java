package com.egelev.nra.api.rest.query;

import com.egelev.nra.Portfolio;
import com.egelev.nra.PortfolioQueryBuilder;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PortfolioQueryFilter {

  @Autowired
  PortfolioQueryBuilder portfolioQueryBuilder;

  public Portfolio applyFilters(
      Portfolio portfolio,
      List<String> exchanges,
      List<String> tickers) {

    return portfolioQueryBuilder.forPortfolio(portfolio)
        .filterByExchange(exchanges)
        .filterByTickerSymbol(tickers)
        .execute();
  }
}
