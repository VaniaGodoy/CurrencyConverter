package br.edu.ifsp.scl.sdm.currencyconverter.model.api

import br.edu.ifsp.scl.sdm.currencyconverter.model.domain.ConversionResult
import br.edu.ifsp.scl.sdm.currencyconverter.model.domain.CurrencyList
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface CurrencyConverterApiService {

  @GET("currencies")
  fun getCurrencies(): Call<CurrencyList>

  @GET("v6/latest/{base}")
    fun convert(
  @Path("base") base: String
  ): Call<ConversionResult>
}