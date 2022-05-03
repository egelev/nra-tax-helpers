package com.egelev.nra.impl;

import com.egelev.nra.Portfolio;
import com.egelev.nra.PortfolioQuery;
import com.egelev.nra.PortfolioQueryBuilder;
import java.math.MathContext;
import org.springframework.stereotype.Service;

@Service
public class PortfolioQueryBuilderImpl implements PortfolioQueryBuilder {

  private final MathContext mathContext;

  public PortfolioQueryBuilderImpl(MathContext mathContext) {
    this.mathContext = mathContext;
  }

  @Override
  public PortfolioQuery forPortfolio(Portfolio portfolio) {
    return new PortfolioQueryImpl(portfolio, mathContext);
  }
}
