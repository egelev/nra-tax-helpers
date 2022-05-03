package com.egelev.nra.api.rest.converter;

import com.egelev.nra.api.rest.resources.InvestmentSecurityResource;
import com.egelev.nra.model.InvestmentSecurity;
import org.springframework.core.convert.converter.Converter;
import org.springframework.stereotype.Service;

@Service
public class InvestmentSecurityConverter implements Converter<InvestmentSecurity, InvestmentSecurityResource> {

  @Override
  public InvestmentSecurityResource convert(InvestmentSecurity source) {
    return new InvestmentSecurityResource(
        source.currency(),
        source.tickerSymbol(),
        source.isin(),
        source.name(),
        source.exchange()
    );
  }
}
