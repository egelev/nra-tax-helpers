package com.egelev.nra.api.rest.controllers;

import com.egelev.nra.Portfolio;
import com.egelev.nra.api.rest.TemporaryUtils;
import com.egelev.nra.api.rest.converter.DividendsConverter;
import com.egelev.nra.api.rest.query.PortfolioQueryFilter;
import com.egelev.nra.api.rest.resources.DividendResource;
import com.egelev.nra.model.Currency;
import com.egelev.nra.model.Dividend;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/dividends")
public class DividendsController {

  @Autowired
  TemporaryUtils temporaryUtils;
  @Autowired
  PortfolioQueryFilter queryFilter;
  @Autowired
  DividendsConverter dividendsConverter;

  @GetMapping
  public List<DividendResource> get(
      @RequestParam(value = "localCurrency", defaultValue = "BGN") Currency localCurrency,
      @RequestParam(value = "exchange", required = false) List<String> exchanges,
      @RequestParam(value = "ticker", required = false) List<String> tickers
  ) {
    Portfolio portfolio = temporaryUtils.buildNewPortfolioFromFiles();
    List<Dividend> dividends = queryFilter.applyFilters(portfolio, exchanges, tickers).getDividends();
    List<DividendResource> result = dividendsConverter.convert(dividends, localCurrency);

    return result;
  }

  @GetMapping("/summary")
  public Map<String, BigDecimal> getSummary(
      @RequestParam(value = "localCurrency", defaultValue = "BGN") Currency localCurrency,
      @RequestParam(value = "exchange", required = false) List<String> exchanges,
      @RequestParam(value = "ticker", required = false) List<String> tickers
  ) {
    List<DividendResource> dividendResources = get(localCurrency, exchanges, tickers);

    BigDecimal totalIncomeLocalCurrency = dividendResources.stream()
        .map(DividendResource::totalIncomeLocalCurrency)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal totalTaxLocalCurrency = dividendResources.stream()
        .map(DividendResource::totalTaxLocalCurrency)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal profit = totalIncomeLocalCurrency.subtract(totalTaxLocalCurrency);

    return Map.of(
        "totalIncomeLocalCurrency", totalIncomeLocalCurrency,
        "totalTaxLocalCurrency", totalTaxLocalCurrency,
        "profit", profit
    );
  }
}
