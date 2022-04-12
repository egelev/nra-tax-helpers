package com.egelev.nra.model;

import java.math.BigDecimal;

public record SellOrder (
    BigDecimal buyPrice,
    Transaction transaction
) { }
