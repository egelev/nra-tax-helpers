package com.egelev.nra.model;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import lombok.Getter;

public record Transaction (
    BigDecimal quantity,
    BigDecimal singlePrice,
    ZonedDateTime timestamp,
    TransactionType type,
    InvestmentSecurity investmentSecurity
) {

  public static Builder builder() {
    return new Builder();
  }

  public static final class Builder {

    private Builder() {}

    BigDecimal quantity;
    BigDecimal singlePrice;
    ZonedDateTime timestamp;
    TransactionType type;
    InvestmentSecurity investmentSecurity;

    public Builder setQuantity(BigDecimal quantity) {
      this.quantity = quantity;
      return this;
    }

    public Builder setSinglePrice(BigDecimal singlePrice) {
      this.singlePrice = singlePrice;
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
          this.quantity,
          this.singlePrice,
          this.timestamp,
          this.type,
          this.investmentSecurity
      );
    }
  }
}
