package br.edu.ifsp.scl.sdm.currencyconverter.service

import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log
import br.edu.ifsp.scl.sdm.currencyconverter.model.api.CurrencyConverterApiClient
import br.edu.ifsp.scl.sdm.currencyconverter.model.livedata.CurrencyConverterLiveData
import java.net.HttpURLConnection.HTTP_OK

class ConvertService : Service() {

  private val binder = ConvertServiceBinder()
  private lateinit var handler: ConvertServiceHandler

  companion object {
    const val FROM_PARAMETER = "from"
    const val TO_PARAMETER = "to"
    const val AMOUNT_PARAMETER = "amount"
  }

  inner class ConvertServiceBinder : Binder() {
    fun getService(): ConvertService = this@ConvertService
  }

  private inner class ConvertServiceHandler(looper: Looper) : Handler(looper) {
    override fun handleMessage(msg: Message) {

      val from = msg.data.getString(FROM_PARAMETER, "")
      val to = msg.data.getString(TO_PARAMETER, "")
      val amount = msg.data.getString(AMOUNT_PARAMETER, "0")?.toDoubleOrNull() ?: 0.0

      Log.d("DEBUG", "ConvertService rodando")
      Log.d("DEBUG", "FROM: $from TO: $to AMOUNT: $amount")

      val response = CurrencyConverterApiClient.service
        .convert(from)
        .execute()

      if (response.isSuccessful) {

        val result = response.body()

        if (result?.rates != null) {

          val rate = result.rates[to]

          Log.d("DEBUG", "Rate: $rate")

          val finalValue = if (rate != null) amount * rate else null

          Log.d("DEBUG", "Final: $finalValue")

          CurrencyConverterLiveData.conversionResultLiveData.postValue(
            result.copy(
              rates = mapOf("RESULT" to (finalValue ?: 0.0))
            )
          )

        } else {
          Log.e("DEBUG", "Rates null")
        }

      } else {
        Log.e("DEBUG", "Erro API: ${response.code()}")
      }
    }
  }

  fun convert(from: String, to: String, amount: String) {
    Log.d("DEBUG", "ConvertService rodando")

    HandlerThread("ConvertThread").apply {
      start()
      handler = ConvertServiceHandler(looper)
    }

    val message = handler.obtainMessage()
    message.data = Bundle().apply {
      putString(FROM_PARAMETER, from)
      putString(TO_PARAMETER, to)
      putString(AMOUNT_PARAMETER, amount)
    }

    handler.sendMessage(message)
  }

  override fun onBind(intent: Intent): IBinder {
    Log.v("ConvertService", "Service started.")
    return binder
  }

  override fun onUnbind(intent: Intent?): Boolean {
    Log.v("ConvertService", "Service stopped.")
    return super.onUnbind(intent)
  }
}