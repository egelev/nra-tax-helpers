package com.egelev.nra.gateways.bnb.currency.converter.impl;

import com.opencsv.bean.CsvBindByPosition;
import java.math.BigDecimal;

public class CsvRateRecord {

  @CsvBindByPosition(position =  0) String timestamp;
  @CsvBindByPosition(position =  1) String fromCurrency;
  @CsvBindByPosition(position =  2) BigDecimal fromAmount;
  @CsvBindByPosition(position =  3) BigDecimal toAmount;


}
