package com.egelev.nra.model;

import java.time.ZonedDateTime;

public record Transaction (
    Worth worth,
    ZonedDateTime timestamp,
    TransactionType type,
    InvestmentSecurity investmentSecurity
) {

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {

    private Builder() {}

    Worth worth;
    ZonedDateTime timestamp;
    TransactionType type;
    InvestmentSecurity investmentSecurity;


    public Builder setWorth(Worth worth) {
      this.worth = worth;
      return this;
    }

    public Builder setTimestamp(ZonedDateTime timestamp) {
      this.timestamp = timestamp;
      return this;
    }

    public Builder setInvestmentSecurity(InvestmentSecurity investmentSecurity) {
      this.investmentSecurity = investmentSecurity;
      return this;
    }

    public Builder setType(TransactionType type) {
      this.type = type;
      return this;
    }

    public Transaction build() {
      return new Transaction(
          this.worth,
          this.timestamp,
          this.type,
          this.investmentSecurity
      );
    }
  }
}
