package com.example.cashflow_app.helpers

import java.text.NumberFormat
import java.util.*

object CurrencyConverter {

    // Format amount with the provided currency code
    fun formatAmount(amount: Double, currencyCode: String): String {
        val locale = getLocaleForCurrency(currencyCode)
        val numberFormat = NumberFormat.getCurrencyInstance(locale)
        return numberFormat.format(amount)
    }

    // Get the Locale for a given currency code
    private fun getLocaleForCurrency(currencyCode: String): Locale {
        return when (currencyCode) {
            "USD" -> Locale("en", "US")
            "EUR" -> Locale("de", "DE")
            "GBP" -> Locale("en", "GB")
            "INR" -> Locale("hi", "IN")
            "LKR" -> Locale("en", "LK")
            else -> Locale.getDefault()  // Default to system locale if currency not found
        }
    }
}
