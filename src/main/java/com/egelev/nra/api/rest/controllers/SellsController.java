package com.egelev.nra.api.rest.controllers;

import com.egelev.nra.Portfolio;
import com.egelev.nra.api.rest.TemporaryUtils;
import com.egelev.nra.api.rest.converter.SellOrderConverter;
import com.egelev.nra.api.rest.query.PortfolioQueryFilter;
import com.egelev.nra.api.rest.resources.SellOrderResource;
import com.egelev.nra.api.rest.resources.SellSummaryResource;
import com.egelev.nra.model.Currency;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/sells")
public class SellsController {

  private static final Logger LOG = LoggerFactory.getLogger(SellsController.class);

  @Autowired
  TemporaryUtils temporaryUtils;
  @Autowired
  PortfolioQueryFilter queryFilter;
  @Autowired
  SellOrderConverter sellOrderConverter;

  @GetMapping
  public List<SellOrderResource> get(
      @RequestParam(value = "localCurrency", defaultValue = "BGN") Currency localCurrency,
      @RequestParam(value = "exchange", required = false) List<String> exchanges,
      @RequestParam(value = "ticker", required = false) List<String> tickers,
      @RequestParam(value = "after", required = false) @DateTimeFormat(iso = ISO.DATE_TIME) ZonedDateTime after,
      @RequestParam(value = "before", required = false) @DateTimeFormat(iso = ISO.DATE_TIME) ZonedDateTime before
      ) {

    Portfolio portfolio = temporaryUtils.buildNewPortfolioFromFiles();

    return queryFilter.applyFilters(portfolio, exchanges, tickers, after, before)
        .getSellOrders().stream()
        .map(so -> sellOrderConverter.convert(so, localCurrency))
        .collect(Collectors.toList());
  }

  @GetMapping("/tickers")
  public List<SellSummaryResource> summarizeTickers(
      @RequestParam(value = "localCurrency", defaultValue = "BGN") Currency localCurrency,
      @RequestParam(value = "exchange", required = false) List<String> exchanges,
      @RequestParam(value = "ticker", required = false) List<String> tickers,
      @RequestParam(value = "after", required = false) @DateTimeFormat(iso = ISO.DATE_TIME) ZonedDateTime after,
      @RequestParam(value = "before", required = false) @DateTimeFormat(iso = ISO.DATE_TIME) ZonedDateTime before
  ) {

    List<SellSummaryResource> sellSummaries = get(localCurrency, exchanges, tickers, after, before)
        .stream()
        .collect(Collectors.groupingBy(so -> so.investmentSecurityResource().isin()))
        .values().stream()
        .map(SellSummaryResource::summarize)
        .collect(Collectors.toList());

    LOG.info("{} summarized securities", sellSummaries.size());

    return sellSummaries;
  }

  @GetMapping("/pal")
  public Map<String, BigDecimal> getProfitAndLoss(
      @RequestParam(value = "localCurrency", defaultValue = "BGN") Currency localCurrency,
      @RequestParam(value = "exchange", required = false) List<String> exchanges,
      @RequestParam(value = "ticker", required = false) List<String> tickers,
      @RequestParam(value = "after", required = false) @DateTimeFormat(iso = ISO.DATE_TIME) ZonedDateTime after,
      @RequestParam(value = "before", required = false) @DateTimeFormat(iso = ISO.DATE_TIME) ZonedDateTime before
  ) {

    List<SellOrderResource> sells = this.get(localCurrency, exchanges, tickers, after, before);

    BigDecimal totalBuyLocalCurrency = sells.stream()
        .map(SellOrderResource::totalBuyLocalCurrency)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal totalSellLocalCurrency = sells.stream()
        .map(SellOrderResource::totalSellLocalCurrency)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal profit = sells.stream()
        .map(SellOrderResource::profit)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    BigDecimal loss = sells.stream()
        .map(SellOrderResource::loss)
        .reduce(BigDecimal.ZERO, BigDecimal::add);

    return Map.of(
        "P&L", profit.add(loss),
        "profit", profit,
        "loss", loss,
        "totalBuyLocalCurrency", totalBuyLocalCurrency,
        "totalSellLocalCurrency", totalSellLocalCurrency
    );
  }

}
