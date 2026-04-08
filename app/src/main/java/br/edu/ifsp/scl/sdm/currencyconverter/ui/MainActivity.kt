package br.edu.ifsp.scl.sdm.currencyconverter.ui

import android.content.*
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.widget.ArrayAdapter
import br.edu.ifsp.scl.sdm.currencyconverter.R
import br.edu.ifsp.scl.sdm.currencyconverter.databinding.ActivityMainBinding
import br.edu.ifsp.scl.sdm.currencyconverter.model.livedata.CurrencyConverterLiveData
import br.edu.ifsp.scl.sdm.currencyconverter.service.ConvertService
import br.edu.ifsp.scl.sdm.currencyconverter.service.CurrenciesService

class MainActivity : AppCompatActivity() {

    private val amb: ActivityMainBinding by lazy {
        ActivityMainBinding.inflate(layoutInflater)
    }

    private val currenciesServiceIntent by lazy {
        Intent(this, CurrenciesService::class.java)
    }

    private var convertService: ConvertService? = null
    var isBound = false

    private val convertServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            convertService = (service as ConvertService.ConvertServiceBinder).getService()
            isBound = true
        }

        override fun onServiceDisconnected(name: ComponentName?) {
            isBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(amb.root)
        setSupportActionBar(amb.mainTb.apply { title = getString(R.string.app_name) })

        var fromQuote = ""
        var toQuote = ""

        val currenciesAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_list_item_1,
            mutableListOf<String>()
        )

        with(amb) {

            fromQuoteMactv.setAdapter(currenciesAdapter)
            fromQuoteMactv.setOnItemClickListener { _, _, _, _ ->
                fromQuote = fromQuoteMactv.text.toString()
            }

            toQuoteMactv.setAdapter(currenciesAdapter)
            toQuoteMactv.setOnItemClickListener { _, _, _, _ ->
                toQuote = toQuoteMactv.text.toString()
            }

            //  BOTÃO AGORA USA SERVICE
            convertBt.setOnClickListener {

                Log.d("DEBUG", "Botão clicado")

                val amount = amb.amountTiet.text.toString()

                if (amount.isNotEmpty() && fromQuote.isNotEmpty() && toQuote.isNotEmpty()) {

                    convertService?.convert(
                        fromQuote,
                        toQuote,
                        amount
                    )

                } else {
                    amb.resultTiet.setText("Preencha os campos")
                }


            }
        }

        //  OBSERVA LISTA DE MOEDAS
        CurrencyConverterLiveData.currenciesLiveData.observe(this) { currencyList ->

            val symbols = currencyList.symbols
            if (symbols.isNullOrEmpty()) return@observe

            currenciesAdapter.notifyDataSetChanged()

            currenciesAdapter.clear()

            val sortedList = symbols.keys.sorted()
            currenciesAdapter.addAll(sortedList)

            sortedList.firstOrNull()?.also {
                amb.fromQuoteMactv.setText(it, false)
                fromQuote = it
            }

            sortedList.lastOrNull()?.also {
                amb.toQuoteMactv.setText(it, false)
                toQuote = it
            }
        }

        //  OBSERVA RESULTADO DA CONVERSÃO
        CurrencyConverterLiveData.conversionResultLiveData.observe(this) { result ->

            Log.d("DEBUG", "Resultado completo: ${result.rates}")

            val value = result.rates?.get("RESULT")

            amb.resultTiet.setText(
                value?.let { "%.2f".format(it) } ?: "Erro na conversão"
            )


        }
        startService(currenciesServiceIntent)
    }

    // CONECTA COM O SERVICE
    override fun onStart() {
        super.onStart()
        Intent(this, ConvertService::class.java).also {
            bindService(it, convertServiceConnection, BIND_AUTO_CREATE)
        }
    }

    override fun onStop() {
        super.onStop()
        if (isBound) {
            unbindService(convertServiceConnection)
            isBound = false
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        stopService(currenciesServiceIntent)
    }
}