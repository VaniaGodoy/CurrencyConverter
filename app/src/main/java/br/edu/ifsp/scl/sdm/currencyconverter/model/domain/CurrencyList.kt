package br.edu.ifsp.scl.sdm.currencyconverter.model.domain

data class CurrencyList(
  val success: Boolean,
  val symbols: Map<String, Symbol>
)

data class Symbol(
  val description: String,
  val code: String
)