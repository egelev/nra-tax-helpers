package com.egelev.nra.impl;


import com.egelev.nra.Portfolio;
import com.egelev.nra.PortfolioQuery;
import com.egelev.nra.model.Currency;
import com.egelev.nra.model.Dividend;
import com.egelev.nra.model.OpenPosition;
import com.egelev.nra.model.SellOrder;
import java.math.MathContext;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class PortfolioQueryImpl implements PortfolioQuery {

  private final Portfolio portfolio;
  private final MathContext mathContext;

  Predicate<String> allowsTickerSymbol;
  Predicate<String> allowsExchange;
  Predicate<Currency> allowsCurrency;
  Predicate<ZonedDateTime> allowsTimestamp;

  public PortfolioQueryImpl(
      Portfolio portfolio,
      MathContext mathContext) {
    this.portfolio = portfolio;
    this.mathContext = mathContext;

    this.allowsTickerSymbol = x -> true;
    this.allowsExchange = x -> true;
    this.allowsCurrency = x -> true;
    this.allowsTimestamp = x -> true;
  }

  public PortfolioQuery filterByTickerSymbol(
      Collection<String> allowedTickerSymbols) {
    this.allowsTickerSymbol = filterListToPredicate(
        Optional.ofNullable(allowedTickerSymbols).orElse(Collections.emptyList()).stream().map(String::toUpperCase).collect(
            Collectors.toSet()),
        String::toUpperCase
    );
    return this;
  }

  public PortfolioQuery filterByExchange(Collection<String> allowedExchanges) {
    this.allowsExchange = filterListToPredicate(
        Optional.ofNullable(allowedExchanges).orElse(Collections.emptyList()).stream().map(String::toUpperCase).collect(Collectors.toSet()),
        String::toUpperCase
    );
    return this;
  }

  public PortfolioQuery filterByCurrency(Collection<Currency> allowedCurrencies) {
    this.allowsCurrency = filterListToPredicate(
        Optional.ofNullable(allowedCurrencies).orElse(Collections.emptyList()),
        Function.identity()
    );
    return this;
  }

  public PortfolioQuery filterByDate(ZonedDateTime after, ZonedDateTime before) {
    Predicate<ZonedDateTime> isAfter = Optional.ofNullable(after)
        .<Predicate<ZonedDateTime>>map(a -> (ZonedDateTime ts) -> ts.isEqual(a) || ts.isAfter(a))
        .orElse(x -> true);

    Predicate<ZonedDateTime> isBefore = Optional.ofNullable(before)
        .<Predicate<ZonedDateTime>>map(b -> (ZonedDateTime ts) -> ts.isBefore(b) || ts.isBefore(b))
        .orElse(x -> true);


    this.allowsTimestamp = isAfter.and(isBefore);
    return this;
  }

  public Portfolio execute() {
    Set<OpenPosition> filteredOpenPositions = this.portfolio.getOpenPositions()
        .stream()
        .filter(op -> allowsTickerSymbol.test(op.investmentSecurity().tickerSymbol()))
        .filter(op -> allowsExchange.test(op.investmentSecurity().exchange()))
        .filter(op -> allowsCurrency.test(op.investmentSecurity().currency()))
        .collect(Collectors.toSet());

    Set<SellOrder> filteredSellOrders = this.portfolio.getSellOrders()
        .stream()
        .filter(so -> allowsTickerSymbol.test(so.transaction().investmentSecurity().tickerSymbol()))
        .filter(so -> allowsExchange.test(so.transaction().investmentSecurity().exchange()))
        .filter(so -> allowsCurrency.test(so.transaction().investmentSecurity().currency()))
        .filter(so -> allowsTimestamp.test(so.transaction().timestamp()))
        .collect(Collectors.toSet());

    Set<Dividend> filteredDividends = this.portfolio.getDividends()
        .stream()
        .filter(d -> allowsTickerSymbol.test(d.transaction().investmentSecurity().tickerSymbol()))
        .filter(d -> allowsExchange.test(d.transaction().investmentSecurity().exchange()))
        .filter(d -> allowsCurrency.test(d.transaction().investmentSecurity().currency()))
        .filter(d -> allowsTimestamp.test(d.transaction().timestamp()))
        .collect(Collectors.toSet());

    return new PortfolioImpl(filteredOpenPositions, filteredSellOrders, filteredDividends, mathContext);
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
