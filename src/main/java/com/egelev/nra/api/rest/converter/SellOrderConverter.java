package com.egelev.nra.api.rest.converter;

import com.egelev.nra.api.rest.resources.SellOrderResource;
import com.egelev.nra.gateways.CurrencyConverterService;
import com.egelev.nra.model.Currency;
import com.egelev.nra.model.SellOrder;
import java.math.BigDecimal;
import java.math.MathContext;
import org.springframework.stereotype.Service;

@Service
public class SellOrderConverter {

  private final InvestmentSecurityConverter investmentSecurityConverter;
  private final CurrencyConverterService currencyConverter;
  private final MathContext mathContext;

  public SellOrderConverter(
      InvestmentSecurityConverter investmentSecurityConverter,
      CurrencyConverterService currencyConverter,
      MathContext mathContext) {
    this.investmentSecurityConverter = investmentSecurityConverter;
    this.currencyConverter = currencyConverter;
    this.mathContext = mathContext;
  }

  public SellOrderResource convert(SellOrder source, Currency localCurrency) {
    BigDecimal totalSellPrice = source.transaction().worth().price();
    BigDecimal totalSellConverted = currencyConverter.convert(
        source.transaction().investmentSecurity().currency(),
        localCurrency,
        totalSellPrice,
        source.transaction().timestamp());

    BigDecimal totalBuyPrice = source.buyPrice().multiply(source.transaction().worth().quantity(), mathContext);
    BigDecimal totalBuyConverted = currencyConverter.convert(
        source.transaction().investmentSecurity().currency(),
        localCurrency,
        totalBuyPrice,
        source.transaction().timestamp()
    );

    BigDecimal profitOrLoss = totalSellConverted.subtract(totalBuyConverted);

    return new SellOrderResource(
        source.transaction().timestamp(),
        investmentSecurityConverter.convert(source.transaction().investmentSecurity()),
        source.transaction().worth().quantity(),
        source.buyPrice(),
        source.transaction().worth().singlePrice(),
        totalBuyPrice,
        totalSellPrice,
        localCurrency,
        totalBuyConverted,
        totalSellConverted,
        profitOrLoss.max(BigDecimal.ZERO),
        profitOrLoss.min(BigDecimal.ZERO)
    );

  }
}
