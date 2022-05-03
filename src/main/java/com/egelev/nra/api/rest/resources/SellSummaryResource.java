package com.egelev.nra.api.rest.resources;

import com.egelev.nra.model.Currency;
import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.Collection;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public record SellSummaryResource (
    Collection<ZonedDateTime> transactionDates,
    InvestmentSecurityResource investmentSecurityResource,
    BigDecimal totalBuy,
    BigDecimal totalSell,
    Currency localCurrency,
    BigDecimal totalBuyLocalCurrency,
    BigDecimal totalSellLocalCurrency,
    BigDecimal profit,
    BigDecimal loss
) {

  public static SellSummaryResource summarize(Collection<SellOrderResource> sellOrderResources) {
    InvestmentSecurityResource investmentSecurityResource = ensureCommonProperty(sellOrderResources, s -> s.investmentSecurityResource());
    Currency localCurrency = ensureCommonProperty(sellOrderResources, s -> s.localCurrency());

    Collection<ZonedDateTime> transactionDates = sellOrderResources.stream().map(SellOrderResource::timestamp).sorted().collect(Collectors.toList());

    BigDecimal totalBuy = sellOrderResources.stream().map(SellOrderResource::totalBuy).reduce(BigDecimal::add).get();
    BigDecimal totalSell = sellOrderResources.stream().map(SellOrderResource::totalSell).reduce(BigDecimal::add).get();

    BigDecimal totalBuyLocalCurrency = sellOrderResources.stream().map(SellOrderResource::totalBuyLocalCurrency).reduce(BigDecimal::add).get();
    BigDecimal totalSellLocalCurrency = sellOrderResources.stream().map(SellOrderResource::totalSellLocalCurrency).reduce(BigDecimal::add).get();

    BigDecimal profitOrLoss = totalSellLocalCurrency.subtract(totalBuyLocalCurrency);

    return new SellSummaryResource(
        transactionDates,
        investmentSecurityResource,
        totalBuy,
        totalSell,
        localCurrency,
        totalBuyLocalCurrency,
        totalSellLocalCurrency,
        profitOrLoss.max(BigDecimal.ZERO),
        profitOrLoss.min(BigDecimal.ZERO)
    );
  }

  private static <T> T ensureCommonProperty(Collection<SellOrderResource> sellOrderResources, Function<SellOrderResource, T> propertyExtractor) {
    List<T> properties = sellOrderResources
        .stream()
        .map(propertyExtractor)
        .distinct()
        .collect(Collectors.toList());

    if (properties.size() != 1) {
      throw new IllegalArgumentException("Differing properties during summarization: " + properties);
    }

    return properties.get(0);
  }

}
