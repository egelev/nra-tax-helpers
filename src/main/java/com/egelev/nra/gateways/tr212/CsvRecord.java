package com.egelev.nra.gateways.tr212;

import com.opencsv.bean.CsvBindByName;
import java.math.BigDecimal;

public class CsvRecord {

  @CsvBindByName(column = "Action") String action;
  @CsvBindByName(column = "Time") String time; // no time-zone datetime
  @CsvBindByName(column = "ISIN") String ISIN;
  @CsvBindByName(column = "Ticker") String ticker;
  @CsvBindByName(column = "Name") String name;
  @CsvBindByName(column = "No. of shares") BigDecimal numberOfShares;
  @CsvBindByName(column = "Price / share") BigDecimal pricePerShare;
  @CsvBindByName(column = "Currency (Price / share)") String currency;
  @CsvBindByName(column = "Exchange rate") String exchangeRate; // big decimal with "not available"
  @CsvBindByName(column = "Result (EUR)") BigDecimal result;
  @CsvBindByName(column = "Total (EUR)") BigDecimal total;
  @CsvBindByName(column = "Withholding tax") BigDecimal withholdingTax;
  @CsvBindByName(column = "Currency (Withholding tax)") String currencyWithholdingTax;
  @CsvBindByName(column = "Charge amount (EUR)") BigDecimal chargeAmountEuro;
  @CsvBindByName(column = "Transaction fee (EUR)") BigDecimal transactionFeeEuro;
  @CsvBindByName(column = "Finra fee (EUR)") BigDecimal finraFeeEuro;
  @CsvBindByName(column = "Notes") String notes;
  @CsvBindByName(column = "ID") String id;
  @CsvBindByName(column = "French transaction tax") BigDecimal frenchTransactionTax;
  @CsvBindByName(column = "Currency conversion fee (EUR)") BigDecimal currencyConversionFeeEuro;

  public String getAction() {
    return action;
  }

  public String getTime() {
    return time;
  }

  public String getISIN() {
    return ISIN;
  }

  public String getTicker() {
    return ticker;
  }

  public String getName() {
    return name;
  }

  public BigDecimal getNumberOfShares() {
    return numberOfShares;
  }

  public BigDecimal getPricePerShare() {
    return pricePerShare;
  }

  public String getCurrency() {
    return currency;
  }

  public String getExchangeRate() {
    return exchangeRate;
  }

  public BigDecimal getResult() {
    return result;
  }

  public BigDecimal getTotal() {
    return total;
  }

  public BigDecimal getWithholdingTax() {
    return withholdingTax;
  }

  public String getCurrencyWithholdingTax() {
    return currencyWithholdingTax;
  }

  public BigDecimal getChargeAmountEuro() {
    return chargeAmountEuro;
  }

  public BigDecimal getTransactionFeeEuro() {
    return transactionFeeEuro;
  }

  public BigDecimal getFinraFeeEuro() {
    return finraFeeEuro;
  }

  public String getNotes() {
    return notes;
  }

  public String getId() {
    return id;
  }

  public BigDecimal getFrenchTransactionTax() {
    return frenchTransactionTax;
  }

  public BigDecimal getCurrencyConversionFeeEuro() {
    return currencyConversionFeeEuro;
  }
}
