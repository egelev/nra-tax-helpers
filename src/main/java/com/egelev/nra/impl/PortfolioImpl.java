package com.egelev.nra.impl;

import static java.util.Objects.isNull;

import com.egelev.nra.Portfolio;
import com.egelev.nra.model.Dividend;
import com.egelev.nra.model.InvestmentSecurity;
import com.egelev.nra.model.OpenPosition;
import com.egelev.nra.model.SellOrder;
import com.egelev.nra.model.Transaction;
import com.egelev.nra.model.TransactionType;
import com.egelev.nra.model.Worth;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PortfolioImpl implements Portfolio {

  private static final Logger LOG = LoggerFactory.getLogger(PortfolioImpl.class);

  private final Map<InvestmentSecurity, OpenPosition> portfolio;
  private final Set<SellOrder> sellOrders;
  private final TreeMap<DividendKey, Dividend> dividends;
  private final MathContext mathContext;

  public PortfolioImpl(MathContext mathContext) {
    this.mathContext = mathContext;
    this.portfolio = new HashMap<>();
    this.sellOrders = new HashSet<>();
    this.dividends = new TreeMap<>();
  }

  PortfolioImpl(
      Set<OpenPosition> openPositions,
      Set<SellOrder> sellOrders,
      Set<Dividend> dividends,
      MathContext mathContext) {
    this.portfolio = openPositions.stream().collect(Collectors.toMap(
        OpenPosition::investmentSecurity,
        x -> x
    ));
    this.sellOrders = new HashSet<>(sellOrders);
    this.dividends = dividends.stream().collect(Collectors.toMap(
        d -> new DividendKey(d.transaction().timestamp(), d.transaction().investmentSecurity()),
        Function.identity(),
        (o, n) -> mergeDividends(o, n),
        TreeMap::new
    ));
    this.mathContext = mathContext;
  }

  @Override
  public OpenPosition addTransaction(Transaction transaction) {
    return switch (transaction.type()) {
      case BUY -> buy(transaction);
      case SELL -> sell(transaction);
      case DIVIDEND -> receiveDividend(transaction);
    };
  }

  @Override
  public Optional<OpenPosition> getOpenPosition(InvestmentSecurity investmentSecurity) {
    return Optional.ofNullable(portfolio.get(investmentSecurity));
  }

  @Override
  public Set<OpenPosition> getOpenPositions() {
    return portfolio.values()
        .stream()
        .map(OpenPosition::new)
        .collect(Collectors.toSet());
  }

  @Override
  public Set<SellOrder> getSellOrders() {
    return new HashSet<>(this.sellOrders);
  }

  @Override
  public List<Dividend> getDividends() {
    return new ArrayList<>(this.dividends.values());
  }

  private OpenPosition buy(Transaction transaction) {
    return this.portfolio.compute(
        transaction.investmentSecurity(),
        (k, currentHolding) -> isNull(currentHolding) ?
            newSecurityHolding(transaction) :
            sum(currentHolding, transaction)
    );
  }

  private OpenPosition newSecurityHolding(Transaction transaction) {
    return new OpenPosition(
        transaction.worth().quantity(),
        transaction.worth().singlePrice(),
        transaction.timestamp(),
        transaction.investmentSecurity()
    );
  }

  private OpenPosition sum(OpenPosition currentHolding, Transaction transaction) {
    BigDecimal newQuantity = currentHolding.quantity().add(transaction.worth().quantity());

    BigDecimal newAveragePrice = currentHolding.quantity().multiply(currentHolding.averageSinglePrice(), mathContext)
        .add(transaction.worth().quantity().multiply(transaction.worth().singlePrice()), mathContext)
        .divide(newQuantity, mathContext);

    ZonedDateTime latestBuyTimestamp = currentHolding.latestBuyTimestamp().isBefore(transaction.timestamp()) ?
        transaction.timestamp() :
        currentHolding.latestBuyTimestamp();

    return new OpenPosition(
        newQuantity,
        newAveragePrice,
        latestBuyTimestamp,
        transaction.investmentSecurity()
    );
  }

  private OpenPosition sell(Transaction transaction) {
    if(!canSell(transaction)) {
      LOG.error(
          "Can not sell security. Not enough amount. Current investment: {}, Transaction: {}",
          portfolio.get(transaction.investmentSecurity()),
          transaction
      );
      return null;
    }

    OpenPosition current = portfolio.get(transaction.investmentSecurity());

    sellOrders.add(
        new SellOrder(
            current.averageSinglePrice(),
            transaction
        )
    );

    OpenPosition newOpenPosition = new OpenPosition(
        current.quantity().subtract(transaction.worth().quantity()),
        current.averageSinglePrice(),
        current.latestBuyTimestamp(),
        current.investmentSecurity()
    );

    if (newOpenPosition.quantity().equals(BigDecimal.ZERO)) {
      portfolio.remove(transaction.investmentSecurity());
    } else {
      portfolio.put(current.investmentSecurity(), newOpenPosition);
    }

    return newOpenPosition;
  }

  private boolean canSell(Transaction transaction) {
    return this.portfolio.containsKey(transaction.investmentSecurity())
        && portfolio.get(transaction.investmentSecurity()).quantity().compareTo(transaction.worth().quantity()) >= 0;
  }

  private OpenPosition receiveDividend(Transaction transaction) {
    this.dividends.put(
        new DividendKey(transaction.timestamp(), transaction.investmentSecurity()),
        new Dividend(transaction));
    return null;
  }

  private static final record DividendKey (
      ZonedDateTime timestamp,
      InvestmentSecurity investmentSecurity
  ) implements Comparable<DividendKey> {

    @Override
    public int compareTo(DividendKey o) {
      return Comparator.comparing(DividendKey::timestamp)
          .thenComparing(dk -> dk.investmentSecurity.isin())
          .compare(this, o);
    }
  }

  private Dividend mergeDividends(Dividend o, Dividend n) {
    BigDecimal newQuantity = o.transaction().worth().quantity().add(n.transaction().worth().quantity(), mathContext);

    BigDecimal averageSinglePrice = o.transaction().worth().price()
        .add(n.transaction().worth().price())
        .divide(newQuantity);

    BigDecimal totalTax = o.transaction().worth().tax().add(n.transaction().worth().tax());

    return new Dividend(
        new Transaction(
            new Worth(
                newQuantity,
                averageSinglePrice,
                totalTax
            ),
            o.transaction().timestamp(),
            TransactionType.DIVIDEND,
            o.transaction().investmentSecurity()
        )
    );
  }

}
