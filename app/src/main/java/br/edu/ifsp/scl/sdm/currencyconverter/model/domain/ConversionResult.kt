package br.edu.ifsp.scl.sdm.currencyconverter.model.domain

data class ConversionResult(
  val result: String,
  val rates: Map<String, Double>
)