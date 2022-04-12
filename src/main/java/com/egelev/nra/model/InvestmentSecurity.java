package com.egelev.nra.model;

public record InvestmentSecurity(
  Currency currency,
  String tickerSymbol,
  String isin,
  String name,
  String exchange
) {

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {

    private Builder() {}

    Currency currency;
    String tickerSymbol;
    String isin;
    String name;
    String exchange;

    public Builder setCurrency(Currency currency) {
      this.currency = currency;
      return this;
    }

    public Builder setTickerSymbol(String tickerSymbol) {
      this.tickerSymbol = tickerSymbol;
      return this;
    }

    public Builder setIsin(String isin) {
      this.isin = isin;
      return this;
    }

    public Builder setName(String name) {
      this.name = name;
      return this;
    }

    public Builder setExchange(String exchange) {
      this.exchange = exchange;
      return this;
    }

    public InvestmentSecurity build() {
      return new InvestmentSecurity(
          this.currency,
          this.tickerSymbol,
          this.isin,
          this.name,
          this.exchange
      );
    }
  }
}
