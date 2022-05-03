package com.egelev.nra.config;

import java.math.MathContext;
import java.math.RoundingMode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AppConfig {


  @Bean
  public MathContext getMathContext(@Value("${bigDecimal.mathContext.precision}") int precision) {
    return new MathContext(precision, RoundingMode.HALF_UP);
  }

}
