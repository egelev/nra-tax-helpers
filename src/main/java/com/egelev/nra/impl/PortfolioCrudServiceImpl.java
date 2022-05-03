package com.egelev.nra.impl;

import com.egelev.nra.Portfolio;
import com.egelev.nra.PortfolioCrudService;
import java.math.MathContext;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Service;

@Service
public class PortfolioCrudServiceImpl implements PortfolioCrudService {

  private final MathContext mathContext;
  private final Map<String, Portfolio> data;

  public PortfolioCrudServiceImpl(MathContext mathContext) {
    this.mathContext = mathContext;
    this.data = new HashMap<>();
  }

  @Override
  public Portfolio create() {
    return create(RandomStringUtils.randomAlphanumeric(64));
  }

  @Override
  public Portfolio create(String id) {
    if (data.containsKey(id)) {
      throw new IllegalArgumentException("Portfolio with already exists, id: " + id);
    }

    return getOrCreate(id);
  }

  @Override
  public Portfolio getOrCreate(String id) {
    return data.computeIfAbsent(id, k -> new PortfolioImpl(mathContext));
  }

  @Override
  public Portfolio get(String id) {
    return data.get(id);
  }

  @Override
  public Collection<Portfolio> list() {
    return new ArrayList<>(data.values());
  }
}
