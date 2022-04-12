package com.egelev.nra.api.rest;

import com.egelev.nra.PortfolioService;
import com.egelev.nra.gateways.FetchMoneyMovements;
import com.egelev.nra.model.SellOrder;
import com.egelev.nra.model.Transaction;
import java.time.ZonedDateTime;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/")
public class HomeController {

  @Autowired
  FetchMoneyMovements transactionsFetcher;
  @Autowired
  PortfolioService portfolioService;

  @GetMapping
  public String get() {

    transactionsFetcher.getTransactions()
        .stream()
        .sorted((t1, t2) -> t1.timestamp().compareTo(t2.timestamp()))
        .forEach(portfolioService::addTransaction);

    return portfolioService.getSellOrders()
        .stream()
        .map(SellOrder::toString)
        .collect(Collectors.joining("<br>"));
  }

}
