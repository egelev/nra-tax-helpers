package com.egelev.nra.model;

import java.math.BigDecimal;

public record Dividend(
   Transaction transaction
) {

  public BigDecimal total() {
    return transaction.worth().total();
  }
}
