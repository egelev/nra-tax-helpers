package com.egelev.nra.api.rest.controllers;

import com.egelev.nra.Portfolio;
import com.egelev.nra.api.rest.TemporaryUtils;
import com.egelev.nra.api.rest.converter.OpenPositionResourceConverter;
import com.egelev.nra.api.rest.query.PortfolioQueryFilter;
import com.egelev.nra.api.rest.resources.OpenPositionResource;
import com.egelev.nra.model.Currency;
import java.time.ZonedDateTime;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.format.annotation.DateTimeFormat.ISO;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/positions")
public class OpenPositionsController {

  @Autowired
  TemporaryUtils temporaryUtils;
  @Autowired
  PortfolioQueryFilter queryFilter;
  @Autowired
  OpenPositionResourceConverter openPositionResourceConverter;


  @GetMapping
  public List<OpenPositionResource> get(
      @RequestParam(value = "localCurrency", defaultValue = "BGN") Currency localCurrency,
      @RequestParam(value = "exchange", required = false) List<String> exchanges,
      @RequestParam(value = "ticker", required = false) List<String> tickers,
      @RequestParam(value = "after", required = false) @DateTimeFormat(iso = ISO.DATE_TIME) ZonedDateTime after,
      @RequestParam(value = "before", required = false) @DateTimeFormat(iso = ISO.DATE_TIME) ZonedDateTime before
  ) {

    Portfolio portfolio = temporaryUtils.buildNewPortfolioFromFiles();
    return queryFilter.applyFilters(portfolio, exchanges, tickers, after, before)
        .getOpenPositions().stream()
        .map(op -> openPositionResourceConverter.convert(op, localCurrency))
        .collect(Collectors.toList());
  }
}
