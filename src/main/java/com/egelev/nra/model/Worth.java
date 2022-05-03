package com.egelev.nra.model;

import java.math.BigDecimal;

public record Worth(
    BigDecimal quantity,
    BigDecimal singlePrice,
    BigDecimal tax
) {

  public Worth(BigDecimal quantity, BigDecimal singlePrice) {
    this(quantity, singlePrice, BigDecimal.ZERO);
  }

  public BigDecimal price() {
    return quantity.multiply(singlePrice);
  }

  public BigDecimal total() {
    return price().subtract(tax);
  }

}
