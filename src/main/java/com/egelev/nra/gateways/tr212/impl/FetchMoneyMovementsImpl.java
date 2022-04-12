package com.egelev.nra.gateways.tr212.impl;

import com.egelev.nra.gateways.FetchMoneyMovements;
import com.egelev.nra.gateways.tr212.CsvParser;
import com.egelev.nra.model.Transaction;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class FetchMoneyMovementsImpl implements FetchMoneyMovements {

  private final Path csvFilePath;
  private final CsvParser csvParser;
  private final CsvToMoneyMovementConverter converter;

  public FetchMoneyMovementsImpl(
      @Value("${trading212.csvFilePath}") String csvFilePath,
      CsvParser csvParser,
      CsvToMoneyMovementConverter converter) {
    this.csvFilePath = Path.of(csvFilePath).toAbsolutePath();
    this.csvParser = csvParser;
    this.converter = converter;
  }

  @Override
  public Collection<Transaction> getTransactions() {
    return csvParser.parse(csvFilePath)
        .stream()
        .map(converter::convert)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

}
