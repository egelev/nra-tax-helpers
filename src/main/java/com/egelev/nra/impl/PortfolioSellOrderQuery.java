package com.egelev.nra.impl;

import com.egelev.nra.Portfolio;
import com.egelev.nra.model.Currency;
import com.egelev.nra.model.SellOrder;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public record PortfolioSellOrderQuery (
    Set<SellOrder> sellOrders
) {

  public static Builder forPortfolio(Portfolio portfolio) {
    return new Builder(portfolio.getSellOrders());
  }

  public static final class Builder {

    private final Set<SellOrder> sellOrders;

    Predicate<String> tickerSymbols;
    Predicate<String> exchanges;
    Predicate<Currency> currencies;

    public Builder(Set<SellOrder> sellOrders) {
      this.sellOrders = sellOrders;

      this.tickerSymbols = x -> true;
      this.exchanges = x -> true;
      this.currencies = x -> true;
    }

    public Builder filterByTickerSymbol(Collection<String> allowedTickerSymbols) {
      this.tickerSymbols = filterListToPredicate(
          Optional.ofNullable(allowedTickerSymbols).orElse(Collections.emptyList()).stream().map(String::toUpperCase).collect(Collectors.toSet()),
          String::toUpperCase
      );
      return this;
    }

    public Builder filterByExchange(Collection<String> allowedExchanges) {
      this.exchanges = filterListToPredicate(
          Optional.ofNullable(allowedExchanges).orElse(Collections.emptyList()).stream().map(String::toUpperCase).collect(Collectors.toSet()),
          String::toUpperCase
      );
      return this;
    }

    public Builder filterByCurrency(Collection<Currency> allowedCurrencies) {
      this.currencies = filterListToPredicate(
          Optional.ofNullable(allowedCurrencies).orElse(Collections.emptyList()),
          Function.identity()
      );
      return this;
    }

    public PortfolioSellOrderQuery execute() {
      Set<SellOrder> filteredSellOrders = this.sellOrders
          .stream()
          .filter(so -> tickerSymbols.test(so.transaction().investmentSecurity().tickerSymbol()))
          .filter(so -> exchanges.test(so.transaction().investmentSecurity().exchange()))
          .filter(so -> currencies.test(so.transaction().investmentSecurity().currency()))
          .collect(Collectors.toSet());

      return new PortfolioSellOrderQuery(filteredSellOrders);
    }

    private <T> Predicate<T> filterListToPredicate(Collection<T> allowList, Function<T, T> peekItem) {
      Set<T> allowListConverted = Optional.ofNullable(allowList)
          .orElse(Collections.emptyList())
          .stream()
          .map(peekItem::apply)
          .collect(Collectors.toSet());

      return s -> allowListConverted.isEmpty() ? true : allowListConverted.contains(peekItem.apply(s));
    }
  }

}
