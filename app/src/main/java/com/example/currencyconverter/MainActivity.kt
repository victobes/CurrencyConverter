package com.example.currencyconverter

import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import com.example.currencyconverter.NetworkUtils.generateURL
import com.example.currencyconverter.NetworkUtils.getResponseFromURL
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONException
import org.json.JSONObject
import java.io.IOException
import java.net.URL

class MainActivity : AppCompatActivity() {

    private var toCurrencyString: String = "RUB"
    private var fromCurrencyString: String = "RUB"
    private var valueString: String = ""

    internal inner class ConverterQueryTask : AsyncTask<URL, Void, String>() {

        override fun doInBackground(vararg urls: URL): String? {
            var response: String? = null
            try {
                response = getResponseFromURL(urls[0])
            } catch (e: IOException) {
                e.printStackTrace()
            }

            return response
        }

        override fun onPostExecute(response: String) {

            var toCurrency: String? = null
            try {
                val jsonResponse = JSONObject(response)
                val jsonRates = jsonResponse.getJSONObject(getString(R.string.json_object_name))
                toCurrency = jsonRates.getString(toCurrencyString)

            } catch (e: JSONException) {
                e.printStackTrace()
            }

            when {
                valueString.isEmpty() -> showMessage(getString(R.string.toast_value_is_empty))
                valueString.equals(".") -> showMessage(getString(R.string.toast_value_is_incorrect))
                valueString.isNotEmpty() && toCurrency != null -> resultTextView.text =
                    String.format("%.4f", toCurrency.toDouble() * valueString.toDouble())
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val currencies = resources.getStringArray(R.array.currencies)

        fromCurrencySpinner.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, currencies)
        fromCurrencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                fromCurrencyString = currencies[position]
            }

        }

        toCurrencySpinner.adapter =
            ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, currencies)
        toCurrencySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                toCurrencyString = currencies[position]
            }

        }

        convertButton.setOnClickListener {
            valueString = valueEditText.text.toString()

            val generatedUrl = generateURL(fromCurrencyString, toCurrencyString)
            ConverterQueryTask().execute(generatedUrl)
        }

    }

    fun showMessage(message: String) {
        Toast.makeText(this@MainActivity, message, LENGTH_LONG).show()
    }
}
