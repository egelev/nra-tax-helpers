package com.egelev.nra.gateways.bnb.currency.converter.impl;

import static com.egelev.nra.model.Currency.BGN;
import static com.egelev.nra.model.Currency.EUR;
import static com.egelev.nra.model.Currency.GBP;
import static com.egelev.nra.model.Currency.GBX;

import com.egelev.nra.gateways.CurrencyConverterService;
import com.egelev.nra.gateways.utils.FileUtils;
import com.egelev.nra.model.Currency;
import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.util.Set;
import javax.annotation.PostConstruct;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class CurrencyConverterServiceImpl implements CurrencyConverterService {

  private static final Logger LOG = LoggerFactory.getLogger(CurrencyConverterServiceImpl.class);
  private static final BigDecimal BD_100 = new BigDecimal(100);

  private final TimeStampedCurrencyRatesState rates;

  private final Path ratesDir;
  private final CurrencyRatesLoader ratesLoader;
  private final MathContext mathContext;

  public CurrencyConverterServiceImpl(
      @Value("${trading212.euroToBgnRate}")BigDecimal euroToBgnRate,
      @Value("${trading212.exchageRatesResourceDir}") Resource ratesDir,
      MathContext mathContext,
      CurrencyRatesLoader ratesLoader) throws IOException {
    this.ratesDir = ratesDir.getFile().toPath().toAbsolutePath();
    this.ratesLoader = ratesLoader;
    this.mathContext = mathContext;
    this.rates = new TimeStampedCurrencyRatesState(mathContext);

    rates.addRate(EUR, BGN, LocalDate.of(1997, 1, 1), euroToBgnRate);
    rates.addRate(GBP, GBX, LocalDate.of(1971, 2, 15), BD_100);
  }

  @PostConstruct
  void loadRates() {
    Set<File> rateFiles = FileUtils.collectFilesRecursively(ratesDir.toFile());
    for (File f : rateFiles) {
      rates.addAll(ratesLoader.parseTimedRates(f.toPath()));
    }
  }

  @Override
  public BigDecimal convert(Currency from, Currency to, BigDecimal amount, ZonedDateTime at) {
    if (from.equals(to)) {
      return amount;
    }

    // NOTE: Do not implement DFS. Require direct rates only.
    // Ugly exception: handle GBX from/to GBP
    BigDecimal correctorRate = BigDecimal.ONE;
    if (GBX.equals(from)) {
      from = GBP;
      correctorRate = BigDecimal.ONE.divide(BD_100, mathContext);
    }
    if (GBX.equals(to)) {
      to = GBP;
      correctorRate = BD_100;
    }


    BigDecimal rate = this.rates.findRate(from, to, at.toLocalDate());

    return amount
        .multiply(correctorRate, mathContext)
        .multiply(rate, mathContext);
  }
}
