package com.example.currencyconverter

import android.net.Uri

import java.io.IOException
import java.net.MalformedURLException
import java.net.URL
import java.util.Scanner

import javax.net.ssl.HttpsURLConnection

object NetworkUtils {

    private const val BANK_API_URL: String = "https://api.exchangeratesapi.io/latest"
    private const val BANK_API_BASE: String = "base"
    private const val BANK_API_SYMBOLS: String = "symbols"

    fun generateURL(fromCurrency: String, toCurrency: String): URL? {
        val buildUri = Uri.parse(BANK_API_URL)
            .buildUpon()
            .appendQueryParameter(BANK_API_BASE, fromCurrency)
            .appendQueryParameter(BANK_API_SYMBOLS, toCurrency)
            .build()

        var url: URL? = null

        try {
            url = URL(buildUri.toString())
        } catch (e: MalformedURLException) {
            e.printStackTrace()
        }

        return url
    }

    @Throws(IOException::class)
    fun getResponseFromURL(url: URL): String? {

        val urlConnection = url.openConnection() as HttpsURLConnection

        try {
            val inputStream = urlConnection.inputStream

            val scanner = Scanner(inputStream)
            scanner.useDelimiter("\\A")

            val hasInput = scanner.hasNext()

            return if (hasInput) {
                scanner.next()
            } else {
                null
            }
        } finally {
            urlConnection.disconnect()
        }

    }

}
