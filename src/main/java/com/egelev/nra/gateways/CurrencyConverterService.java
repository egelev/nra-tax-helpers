package com.egelev.nra.gateways;

import com.egelev.nra.model.Currency;
import java.math.BigDecimal;
import java.time.ZonedDateTime;

public interface CurrencyConverterService {

  BigDecimal convert(Currency from, Currency to, BigDecimal amount, ZonedDateTime at);

}
