package com.egelev.nra.gateways.tr212.impl;

import static java.util.Objects.isNull;

import com.egelev.nra.gateways.tr212.CsvRecord;
import com.egelev.nra.gateways.tr212.SecurityExchangeResolver;
import com.egelev.nra.model.Currency;
import com.egelev.nra.model.InvestmentSecurity;
import com.egelev.nra.model.Transaction;
import com.egelev.nra.model.TransactionType;
import com.egelev.nra.model.Worth;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Component;

@Component
public class CsvToTransactionConverter implements Converter<CsvRecord, Transaction> {

  static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("y-M-d H:m:s");

  private final SecurityExchangeResolver securityExchangeResolver;

  public CsvToTransactionConverter(
      SecurityExchangeResolver securityExchangeResolver) {
    this.securityExchangeResolver = securityExchangeResolver;
  }


  @Override
  public Transaction convert(CsvRecord source) {
    Currency currency = convertCurrency(source.getCurrency());
    TransactionType type = convertType(source.getAction());
    if (Currency.UNKNOWN.equals(currency) || isNull(type)) {
      return null;
    }

    InvestmentSecurity investmentSecurity = InvestmentSecurity.builder()
        .setCurrency(currency)
        .setIsin(source.getISIN())
        .setTickerSymbol(source.getTicker())
        .setName(source.getName())
        .setExchange(convertExchange(source))
        .build();

    Worth worth = new Worth(
        source.getNumberOfShares(),
        source.getPricePerShare(),
        source.getWithholdingTax());

    return Transaction.builder()
        .setWorth(worth)
        .setTimestamp(convertTimeStamp(source.getTime()))
        .setType(type)
        .setInvestmentSecurity(investmentSecurity)
        .build();

  }

  private TransactionType convertType(String action) {
    String actionString = action.toLowerCase();
    if(actionString.contains("dividend")) {
      return TransactionType.DIVIDEND;
    } else if (actionString.contains("buy")) {
      return TransactionType.BUY;
    } else if (actionString.contains("sell")) {
      return TransactionType.SELL;
    }
    return null;
  }

  private ZonedDateTime convertTimeStamp(String time) {
    LocalDateTime localDateTime = LocalDateTime.parse(time, DATE_TIME_FORMATTER);
    // NOTE: Trading212 does not provide zone-id info. It looks like the zone-id of the user so use
    // the system default.
    return localDateTime.atZone(ZoneId.systemDefault());
  }

  private Currency convertCurrency(String currency) {
    String symbol = currency.toUpperCase();
    try{
      return Currency.valueOf(symbol);
    } catch (IllegalArgumentException e) {
      return Currency.UNKNOWN;
    }
  }

  private String convertExchange(CsvRecord source) {
    return securityExchangeResolver.resolveExchange(source.getName());
  }

}
