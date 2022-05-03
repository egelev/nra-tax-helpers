package com.egelev.nra;

import java.util.Collection;

public interface PortfolioCrudService {
  Portfolio create();
  Portfolio create(String id);
  Portfolio getOrCreate(String id);
  Portfolio get(String id);
  Collection<Portfolio> list();
}
