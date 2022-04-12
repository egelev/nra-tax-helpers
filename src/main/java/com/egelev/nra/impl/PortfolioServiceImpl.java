package com.egelev.nra.impl;

import static java.util.Objects.isNull;

import com.egelev.nra.PortfolioService;
import com.egelev.nra.model.InvestmentSecurity;
import com.egelev.nra.model.SecurityHolding;
import com.egelev.nra.model.SellOrder;
import com.egelev.nra.model.Transaction;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class PortfolioServiceImpl implements PortfolioService {

  private final Map<InvestmentSecurity, SecurityHolding> portfolio;
  private final Set<SellOrder> sellOrders;

  public PortfolioServiceImpl() {
    this.portfolio = new HashMap<>();
    this.sellOrders = new HashSet<>();
  }

  @Override
  public SecurityHolding addTransaction(Transaction transaction) {
    return switch (transaction.type()) {
      case BUY -> buy(transaction);
      case SELL -> sell(transaction);
      case DIVIDEND -> receiveDividend(transaction);
      default -> null;
    };
  }

  @Override
  public Optional<SecurityHolding> getSecurity(InvestmentSecurity investmentSecurity) {
    return Optional.ofNullable(portfolio.get(investmentSecurity));
  }

  @Override
  public Set<SecurityHolding> getSecurityHoldings() {
    return new HashSet<>(portfolio.values());
  }

  @Override
  public Set<SellOrder> getSellOrders() {
    return new HashSet<>(this.sellOrders);
  }

  @Override
  public Map<InvestmentSecurity, BigDecimal> getDividends() {
    return portfolio.entrySet().stream()
        .collect(Collectors.toMap(
            Entry::getKey,
            entry -> entry.getValue().dividend()
        ));
  }

  private SecurityHolding buy(Transaction transaction) {
    return this.portfolio.compute(
        transaction.investmentSecurity(),
        (k, currentHolding) -> isNull(currentHolding) ?
            newSecurityHolding(transaction) :
            sum(currentHolding, transaction)
    );
  }

  private SecurityHolding newSecurityHolding(Transaction transaction) {
    return new SecurityHolding(
        transaction.quantity(),
        transaction.singlePrice(),
        transaction.investmentSecurity()
    );
  }

  private SecurityHolding sum(SecurityHolding currentHolding, Transaction transaction) {
    BigDecimal newQuantity = currentHolding.quantity().add(transaction.quantity());
    BigDecimal newAveragePrice = currentHolding.quantity().multiply(currentHolding.averageSinglePrice())
        .add(transaction.quantity().multiply(transaction.singlePrice()))
        .divide(newQuantity, 16, RoundingMode.HALF_UP);
    return new SecurityHolding(
        newQuantity,
        newAveragePrice,
        transaction.investmentSecurity()
    );
  }

  private SecurityHolding sell(Transaction transaction) {
    if(!canSell(transaction)) {
      return null;
//      throw new IllegalStateException(String.format(
//          "Can not sell security. Not enough amount. Current investment: %s, Transaction: %s",
//          transaction,
//          portfolio.get(transaction.investmentSecurity())
//      ));
    }

    SecurityHolding current = portfolio.get(transaction.investmentSecurity());

    sellOrders.add(
        new SellOrder(
            current.averageSinglePrice(),
            transaction
        )
    );

    SecurityHolding newSecurityHolding = new SecurityHolding(
        current.quantity().min(transaction.quantity()),
        current.averageSinglePrice(),
        current.investmentSecurity()
    );

    portfolio.put(current.investmentSecurity(), newSecurityHolding);
    return newSecurityHolding;
  }

  private boolean canSell(Transaction transaction) {
    return this.portfolio.containsKey(transaction.investmentSecurity())
        && portfolio.get(transaction.investmentSecurity()).quantity().compareTo(transaction.quantity()) >= 0;
  }

  private SecurityHolding receiveDividend(Transaction transaction) {
    if(!portfolio.containsKey(transaction.investmentSecurity())) {
//      throw new IllegalStateException("Can not receive dividend for lacking security" + transaction);
      return null;
    }

    BigDecimal addition = transaction.quantity().multiply(transaction.singlePrice());
    return portfolio.computeIfPresent(transaction.investmentSecurity(),
        (k, current) -> new SecurityHolding(
            current.quantity(),
            current.averageSinglePrice(),
            current.dividend().add(addition),
            current.investmentSecurity()
        ));
  }
}
