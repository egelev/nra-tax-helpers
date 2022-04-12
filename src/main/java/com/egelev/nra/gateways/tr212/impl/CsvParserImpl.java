package com.egelev.nra.gateways.tr212.impl;

import com.egelev.nra.gateways.tr212.CsvParser;
import com.egelev.nra.gateways.tr212.CsvRecord;
import com.opencsv.bean.CsvToBean;
import com.opencsv.bean.CsvToBeanBuilder;
import com.opencsv.bean.HeaderColumnNameMappingStrategy;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class CsvParserImpl implements CsvParser {

  @Override
  public List<CsvRecord> parse(Path path) {

    HeaderColumnNameMappingStrategy<CsvRecord> mappingStrategy = new HeaderColumnNameMappingStrategy<>();
    mappingStrategy.setType(CsvRecord.class);
    try(Reader reader = Files.newBufferedReader(path)) {
      CsvToBean<CsvRecord> parser = new CsvToBeanBuilder<CsvRecord>(reader)
          .withIgnoreEmptyLine(true)
          .withMappingStrategy(mappingStrategy)
          .build();

      return parser.parse();

    } catch (IOException e) {
      e.printStackTrace();
    }
    return null;
  }
}
