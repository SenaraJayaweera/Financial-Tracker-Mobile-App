package com.example.cashflow_app.helpers

import android.content.Context
import android.content.SharedPreferences
import com.example.cashflow_app.helpers.CurrencyConverter
import com.example.cashflow_app.models.Transaction
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

object SharedPrefHelper {
    private const val PREFS_NAME = "FinanceTrackerPrefs"
    private const val TRANSACTIONS_KEY = "transactions_key"
    private const val INITIAL_BUDGET_KEY = "initial_budget_key"
    private const val CURRENCY_TYPE_KEY = "currency_type_key"

    // Save transactions to SharedPreferences
    fun saveTransactions(context: Context, transactions: List<Transaction>) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        val json = Gson().toJson(transactions)
        editor.putString(TRANSACTIONS_KEY, json)
        editor.apply()
    }

    // Get transactions from SharedPreferences
    fun getTransactions(context: Context): List<Transaction> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(TRANSACTIONS_KEY, null)
        return if (json != null) {
            val type = object : TypeToken<List<Transaction>>() {}.type
            Gson().fromJson(json, type)
        } else {
            emptyList()
        }
    }

    // Calculate current budget in base currency (no conversion)
    fun calculateCurrentBudget(context: Context): Double {
        val transactions = getTransactions(context)
        val income = transactions.filter { it.type == "Income" }.sumOf { it.amount }
        val expenses = transactions.filter { it.type == "Expense" }.sumOf { it.amount }
        val initialBudget = getInitialBudget(context)
        return initialBudget + income - expenses
    }

    // Save the initial budget to SharedPreferences
    fun saveInitialBudget(context: Context, amount: Double) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putFloat(INITIAL_BUDGET_KEY, amount.toFloat())
        editor.apply()
    }

    // Get the initial budget from SharedPreferences
    fun getInitialBudget(context: Context): Double {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getFloat(INITIAL_BUDGET_KEY, 0f).toDouble()
    }

    // Save the selected currency type to SharedPreferences
    fun saveCurrencyType(context: Context, currency: String) {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val editor = prefs.edit()
        editor.putString(CURRENCY_TYPE_KEY, currency)
        editor.apply()
    }

    // Get the selected currency type from SharedPreferences
    fun getCurrencyType(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(CURRENCY_TYPE_KEY, "Rs.") ?: "Rs."
    }

    // Get base currency (for formatting)
    fun getBaseCurrency(context: Context): String {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        return prefs.getString(CURRENCY_TYPE_KEY, "USD") ?: "USD"
    }

    // Format amount using base currency
    fun getFormattedAmount(context: Context, amount: Double): String {
        val currencyCode = getBaseCurrency(context)
        return CurrencyConverter.formatAmount(amount, currencyCode)
    }

    // Export data to JSON format
    fun exportDataToJson(context: Context): String {
        val transactions = getTransactions(context)
        val initialBudget = getInitialBudget(context)
        val data = mapOf(
            "transactions" to transactions,
            "initial_budget" to initialBudget
        )
        return Gson().toJson(data)
    }

    // Import data from JSON string
    fun importDataFromJson(context: Context, json: String): Boolean {
        return try {
            val type = object : TypeToken<Map<String, Any>>() {}.type
            val data: Map<String, Any> = Gson().fromJson(json, type)

            val transactions = Gson().fromJson<List<Transaction>>(
                Gson().toJson(data["transactions"]),
                object : TypeToken<List<Transaction>>() {}.type
            )

            val initialBudget = (data["initial_budget"] as Double)

            saveTransactions(context, transactions)
            saveInitialBudget(context, initialBudget)
            true
        } catch (e: Exception) {
            false
        }
    }
}
