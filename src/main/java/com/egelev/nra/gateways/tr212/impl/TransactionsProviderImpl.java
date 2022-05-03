package com.egelev.nra.gateways.tr212.impl;

import com.egelev.nra.gateways.TransactionsProvider;
import com.egelev.nra.gateways.tr212.CsvParser;
import com.egelev.nra.gateways.utils.FileUtils;
import com.egelev.nra.model.Transaction;
import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class TransactionsProviderImpl implements TransactionsProvider {

  private final Path csvFilePath;
  private final CsvParser csvParser;
  private final CsvToTransactionConverter converter;

  public TransactionsProviderImpl(
      @Value("${trading212.csvFilePath}") String csvFilePath,
      CsvParser csvParser,
      CsvToTransactionConverter converter) {
    this.csvFilePath = Path.of(csvFilePath).toAbsolutePath();
    this.csvParser = csvParser;
    this.converter = converter;
  }

  @Override
  public Collection<Transaction> getTransactions() {
    return FileUtils.collectFilesRecursively(csvFilePath.toFile()).stream()
        .map(File::toPath)
        .map(csvParser::parse)
        .flatMap(List::stream)
        .map(converter::convert)
        .filter(Objects::nonNull)
        .collect(Collectors.toList());
  }

}
