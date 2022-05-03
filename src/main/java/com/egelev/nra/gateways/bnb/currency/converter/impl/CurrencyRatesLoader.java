package com.egelev.nra.gateways.bnb.currency.converter.impl;

import static java.util.Objects.nonNull;

import com.egelev.nra.model.Currency;
import com.opencsv.bean.ColumnPositionMappingStrategy;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import java.io.BufferedReader;
import java.math.BigDecimal;
import java.math.MathContext;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class CurrencyRatesLoader {

  private static final Logger LOG = LoggerFactory.getLogger(CurrencyRatesLoader.class);

  private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("d.M.y");

  private final MathContext mathContext;

  public CurrencyRatesLoader(MathContext mathContext) {
    this.mathContext = mathContext;
  }

  public TimeStampedCurrencyRatesState parseTimedRates(Path path) {
    TimeStampedCurrencyRatesState timedRates = new TimeStampedCurrencyRatesState(mathContext);
    try(BufferedReader reader = Files.newBufferedReader(path);) {

      // skip 1st line - Bulgarian national bank description header
      reader.readLine();
      // skip 2nd line - headers
      reader.readLine();

      ColumnPositionMappingStrategy ms = new ColumnPositionMappingStrategy();
      ms.setType(CsvRateRecord.class);
      CsvToBean<CsvRateRecord> csvToBean = new CsvToBeanBuilder<CsvRateRecord>(reader)
          .withMappingStrategy(ms)
          .build();

      csvToBean.parse()
          .stream()
          .filter(this::isValidRecord)
          .forEach(record -> {
            LocalDate localDate = parseDate(record.timestamp);
            Currency from = parseCurrency(record.fromCurrency);
            BigDecimal rate = record.toAmount.divide(record.fromAmount, mathContext);
            timedRates.addRate(from, Currency.BGN, localDate, rate);
          });

    } catch (Exception e) {
      e.printStackTrace();
    }
    return timedRates;
  }

  private Currency parseCurrency(String s) {
    return Optional.ofNullable(Currency.valueOf(s.trim().toUpperCase()))
        .orElseGet(() -> {
          LOG.error("Can not parse currency: {}", s);
          return Currency.UNKNOWN;
        });
  }

  private LocalDate parseDate(String date) {
    return LocalDate.parse(date.trim(), FORMATTER);
  }

  boolean isValidRecord(CsvRateRecord record) {
     boolean isValid = nonNull(record.fromCurrency) && !record.fromCurrency.isBlank()
        && nonNull(record.timestamp) && !record.timestamp.isBlank()
        && nonNull(record.fromAmount)
        && nonNull(record.toAmount);
     if (!isValid) {
       LOG.warn("Skip invalid currency rate csv row: {}", record);
     }
     return isValid;
  }

}
