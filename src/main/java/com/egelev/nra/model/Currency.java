package com.egelev.nra.model;

/**
 * https://www.iban.com/currency-codes
 */
public enum Currency {
  BGN,
  EUR,
  USD,
  GBP,
  // Penny sterling - 1/100 of the pound sterling
  GBX,

  UNKNOWN;
  // TODO: Add more if needed
}
