package com.egelev.nra.api.rest.converter;

import static java.math.BigDecimal.ZERO;

import com.egelev.nra.api.rest.resources.DividendResource;
import com.egelev.nra.api.rest.resources.DividendResource.Payment;
import com.egelev.nra.gateways.CurrencyConverterService;
import com.egelev.nra.model.Currency;
import com.egelev.nra.model.Dividend;
import com.egelev.nra.model.InvestmentSecurity;
import java.math.BigDecimal;
import java.math.MathContext;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class DividendsConverter {

  private final CurrencyConverterService currencyConverterService;
  private final InvestmentSecurityConverter investmentSecurityConverter;
  private final MathContext mathContext;


  public DividendsConverter(
      CurrencyConverterService currencyConverterService,
      InvestmentSecurityConverter investmentSecurityConverter,
      MathContext mathContext) {
    this.currencyConverterService = currencyConverterService;
    this.investmentSecurityConverter = investmentSecurityConverter;
    this.mathContext = mathContext;
  }

  public List<DividendResource> convert(List<Dividend> dividends, Currency localCurrency) {
    return dividends.stream()
        .collect(Collectors.groupingBy(d -> d.transaction().investmentSecurity()))
        .entrySet().stream()
        .map(e -> initDividendResource(e.getKey(), e.getValue(), localCurrency))
        .collect(Collectors.toList());
  }

  DividendResource initDividendResource(InvestmentSecurity investmentSecurity, List<Dividend> dividends, Currency localCurrency) {
    List<Payment> payments = dividends.stream()
        .map(d -> new Payment(
            d.transaction().timestamp(),
            d.transaction().worth().price(),
            d.transaction().worth().tax()
        ))
        .collect(Collectors.toList());

    BigDecimal totalIncome = payments.stream().map(Payment::income).reduce(ZERO, BigDecimal::add);
    BigDecimal totalTax = payments.stream().map(Payment::tax).reduce(ZERO, BigDecimal::add);

    List<Payment> paymentsLocalCurrency = dividends.stream()
        .map(d -> new Payment(
            d.transaction().timestamp(),
            currencyConverterService.convert(
                d.transaction().investmentSecurity().currency(),
                localCurrency,
                d.transaction().worth().price(),
                d.transaction().timestamp()
            ),
            currencyConverterService.convert(
                d.transaction().investmentSecurity().currency(),
                localCurrency,
                d.transaction().worth().tax(),
                d.transaction().timestamp()
            )
        ))
        .collect(Collectors.toList());

    BigDecimal totalIncomeLocalCurrency = paymentsLocalCurrency.stream().map(Payment::income).reduce(ZERO, BigDecimal::add);
    BigDecimal totalTaxLocalCurrency = paymentsLocalCurrency.stream().map(Payment::tax).reduce(ZERO, BigDecimal::add);

    return new DividendResource(
        investmentSecurityConverter.convert(investmentSecurity),
        payments,
        totalIncome,
        totalTax,
        localCurrency,
        paymentsLocalCurrency,
        totalIncomeLocalCurrency,
        totalTaxLocalCurrency,
        remainingTax(totalIncomeLocalCurrency, totalTaxLocalCurrency),
        totalIncomeLocalCurrency.multiply(new BigDecimal(0.05))
    );
  }

  private BigDecimal remainingTax(BigDecimal totalIncomeLocalCurrency,
      BigDecimal totalTaxLocalCurrency) {
    BigDecimal _5p = totalIncomeLocalCurrency.multiply(new BigDecimal(0.05));
    if (_5p.compareTo(totalTaxLocalCurrency) > 0) {
      return _5p.subtract(totalTaxLocalCurrency);
    }
    return ZERO;
  }

}
