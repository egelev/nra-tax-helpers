package com.egelev.nra.gateways;

import com.egelev.nra.model.Transaction;
import java.util.Collection;

public interface TransactionsProvider {

  Collection<Transaction> getTransactions();

}
