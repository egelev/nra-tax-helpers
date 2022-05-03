package com.egelev.nra.gateways.bnb.currency.converter.impl;

import com.egelev.nra.model.Currency;
import java.math.BigDecimal;
import java.math.MathContext;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.TreeMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TimeStampedCurrencyRatesState {

  private static final Logger LOG = LoggerFactory.getLogger(TimeStampedCurrencyRatesState.class);

  // from -> (to -> (timestamp -> rate) ) )
  private final Map<Currency, Map<Currency, TreeMap<LocalDate, BigDecimal>>> timedRates;

  private final MathContext mathContext;

  public TimeStampedCurrencyRatesState(MathContext mathContext) {
    this.mathContext = mathContext;
    timedRates = new HashMap<>();
  }

  public void addRate(Currency from, Currency to, LocalDate at, BigDecimal rate) {
    putRate(from, to, at, rate);
    BigDecimal inverseRate = BigDecimal.ONE.divide(rate, mathContext);
    putRate(to, from, at, inverseRate);
  }

  private void putRate(Currency from, Currency to, LocalDate at, BigDecimal rate) {
    timedRates.computeIfAbsent(from, f -> new HashMap<>())
        .computeIfAbsent(to, t -> new TreeMap<>())
        .put(at, rate);
  }

  public BigDecimal findRate(Currency from, Currency to, LocalDate at) {
    Entry<LocalDate, BigDecimal> timedRateEntry = timedRates
        .getOrDefault(from, new HashMap<>())
        .getOrDefault(to, new TreeMap<>())
        .floorEntry(at);

    BigDecimal rate = Optional.ofNullable(timedRateEntry)
        .map(Entry::getValue)
        .orElseGet(() -> {
          LOG.error("No rate found for {}-{} currency pair at {}", from, to, at);
          return BigDecimal.ZERO;
        });

    return rate;
  }

  public void addAll(TimeStampedCurrencyRatesState state) {
    state.timedRates.entrySet().forEach(
        fe -> {
          Currency from = fe.getKey();
          fe.getValue().entrySet().forEach(
              te -> {
                Currency to = te.getKey();
                te.getValue().entrySet().forEach(
                    de -> {
                      LocalDate at = de.getKey();
                      BigDecimal rate = de.getValue();

                      putRate(from, to, at, rate);
                    }
                );
              }
          );
        }
    );
  }
}
