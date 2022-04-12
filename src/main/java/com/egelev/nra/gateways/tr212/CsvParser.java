package com.egelev.nra.gateways.tr212;

import java.nio.file.Path;
import java.util.List;

public interface CsvParser {

  List<CsvRecord> parse(Path path);

}
