package com.egelev.nra.api.rest.converter;

import com.egelev.nra.api.rest.resources.OpenPositionResource;
import com.egelev.nra.gateways.CurrencyConverterService;
import com.egelev.nra.model.Currency;
import com.egelev.nra.model.OpenPosition;
import java.math.BigDecimal;
import java.math.MathContext;
import org.springframework.stereotype.Service;

@Service
public class OpenPositionResourceConverter {

  private final InvestmentSecurityConverter investmentSecurityConverter;
  private final CurrencyConverterService currencyConverterService;
  private final MathContext mathContext;

  public OpenPositionResourceConverter(
      InvestmentSecurityConverter investmentSecurityConverter,
      CurrencyConverterService currencyConverterService,
      MathContext mathContext) {
    this.investmentSecurityConverter = investmentSecurityConverter;
    this.currencyConverterService = currencyConverterService;
    this.mathContext = mathContext;
  }

  public OpenPositionResource convert(OpenPosition source, Currency localCurrency) {
    BigDecimal total = source.averageSinglePrice().multiply(source.quantity(), mathContext);
    BigDecimal totalLocalized = currencyConverterService.convert(
        source.investmentSecurity().currency(),
        localCurrency,
        total,
        source.latestBuyTimestamp()
    );

    return new OpenPositionResource(
        source.latestBuyTimestamp(),
        investmentSecurityConverter.convert(source.investmentSecurity()),
        source.quantity(),
        source.averageSinglePrice(),
        total,
        localCurrency,
        totalLocalized
    );
  }
}
