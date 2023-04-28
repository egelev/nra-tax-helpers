package com.egelev.nra.gateways.tr212;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class SecurityExchangeResolver {

  private static final Pattern p = Pattern.compile("^(?<key>[\\w-'&\\\\(\\\\)\\s]+)=(?<value>[\\w-'&\\s]*)$", Pattern.CASE_INSENSITIVE);
  private static final String UNKNOWN = "Unknown";

  private final Path mapFilePath;
  private Map<String, String> properties;

  public SecurityExchangeResolver(@Value("${trading212.exchangeMapFilePath}") Resource mapFilePath)
      throws IOException {
    this.mapFilePath = mapFilePath.getFile().toPath().toAbsolutePath();
    this.properties = new HashMap<>();
  }

  @PostConstruct
  private void loadProperties() {
    try(BufferedReader reader = new BufferedReader(new FileReader(mapFilePath.toFile()))){
      reader.lines()
          .forEach(l -> {
            Matcher m = p.matcher(l);
            if(m.find()) {
              properties.put(m.group("key").trim(), m.group("value").trim());
            }
          });
    } catch (FileNotFoundException e) {
      e.printStackTrace();
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public String resolveExchange(String security) {
    return properties.getOrDefault(security.trim(), UNKNOWN);
  }


}
