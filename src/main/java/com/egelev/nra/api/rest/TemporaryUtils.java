package com.egelev.nra.api.rest;

import com.egelev.nra.Portfolio;
import com.egelev.nra.PortfolioCrudService;
import com.egelev.nra.gateways.TransactionsProvider;
import com.egelev.nra.model.Transaction;
import java.util.Comparator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

// Temporary convenience approach. Add a portfolio controller
@Service
public class TemporaryUtils {
  @Autowired
  private final TransactionsProvider transactionsFetcher;
  @Autowired
  private final PortfolioCrudService portfolioCrudService;

  TemporaryUtils(
      TransactionsProvider transactionsFetcher,
      PortfolioCrudService portfolioCrudService) {
    this.transactionsFetcher = transactionsFetcher;
    this.portfolioCrudService = portfolioCrudService;
  }

  public Portfolio buildNewPortfolioFromFiles() {

    Portfolio portfolio = portfolioCrudService.create();

    transactionsFetcher.getTransactions()
        .stream()
        .sorted(Comparator.comparing(Transaction::timestamp))
        .forEach(portfolio::addTransaction);

    return portfolio;
  }

}
