package com.example.cashflow_app

import android.annotation.SuppressLint
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.anychart.AnyChart
import com.anychart.AnyChartView
import com.anychart.chart.common.dataentry.DataEntry
import com.anychart.chart.common.dataentry.ValueDataEntry
import com.example.cashflow_app.R
import com.example.cashflow_app.helpers.CurrencyConverter
import com.example.cashflow_app.helpers.SharedPrefHelper
import com.example.cashflow_app.models.Transaction
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class BudgetScreen : AppCompatActivity() {

    private lateinit var chartView: AnyChartView
    private lateinit var textViewTotalBudget: TextView
    private lateinit var textViewTotalIncome: TextView
    private lateinit var textViewTotalExpenses: TextView
    private lateinit var textViewRemainingBalance: TextView
    private lateinit var textViewBudgetWarning: TextView

    // BroadcastReceiver for currency changes
    private val currencyChangeReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent?) {
            updateChartAndSummary()
        }
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_budget_screen)

        chartView = findViewById(R.id.any_chart_view)
        textViewTotalBudget = findViewById(R.id.textViewTotalBudget)
        textViewTotalIncome = findViewById(R.id.textViewTotalIncome)
        textViewTotalExpenses = findViewById(R.id.textViewTotalExpenses)
        textViewRemainingBalance = findViewById(R.id.textViewRemainingBalance)
        textViewBudgetWarning = findViewById(R.id.textViewBudgetWarning)

        setupNavigation()
    }

    override fun onResume() {
        super.onResume()
        registerCurrencyChangeReceiver()
        updateChartAndSummary()
    }

    @SuppressLint("UnspecifiedRegisterReceiverFlag")
    private fun registerCurrencyChangeReceiver() {
        val filter = IntentFilter("CURRENCY_CHANGED")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            registerReceiver(currencyChangeReceiver, filter, Context.RECEIVER_NOT_EXPORTED)
        } else {
            registerReceiver(currencyChangeReceiver, filter)
        }
    }

    override fun onPause() {
        super.onPause()
        unregisterReceiver(currencyChangeReceiver)
    }

    private fun updateChartAndSummary() {
        val pie = AnyChart.pie()
        val transactions = SharedPrefHelper.getTransactions(this)
        val monthlyTransactions = filterCurrentMonth(transactions)

        val (totalIncome, totalExpense) = calculateTotals(monthlyTransactions)
        val balance = totalIncome - totalExpense
        val initialBudget = SharedPrefHelper.getInitialBudget(this)
        val totalBudget = initialBudget + totalIncome
        val currencyCode = SharedPrefHelper.getBaseCurrency(this)

        updateTextViews(totalBudget, totalIncome, totalExpense, balance, currencyCode)
        updateBudgetWarning(totalExpense, totalBudget)
        updatePieChart(pie, totalIncome, totalExpense, balance)

        chartView.setChart(pie)
    }

    private fun calculateTotals(transactions: List<Transaction>): Pair<Double, Double> {
        var totalIncome = 0.0
        var totalExpense = 0.0

        for (transaction in transactions) {
            when (transaction.type) {
                "Income" -> totalIncome += transaction.amount
                "Expense" -> totalExpense += transaction.amount
            }
        }
        return Pair(totalIncome, totalExpense)
    }

    private fun updateTextViews(
        totalBudget: Double,
        totalIncome: Double,
        totalExpense: Double,
        balance: Double,
        currencyCode: String
    ) {
        textViewTotalBudget.text = "Total Budget: ${CurrencyConverter.formatAmount(totalBudget, currencyCode)}"
        textViewTotalIncome.text = "Total Income: ${CurrencyConverter.formatAmount(totalIncome, currencyCode)}"
        textViewTotalExpenses.text = "Total Expenses: ${CurrencyConverter.formatAmount(totalExpense, currencyCode)}"
        textViewRemainingBalance.text = "Remaining Balance: ${CurrencyConverter.formatAmount(balance, currencyCode)}"
    }

    private fun updateBudgetWarning(totalExpense: Double, totalBudget: Double) {
        textViewBudgetWarning.apply {
            when {
                totalExpense > totalBudget -> {
                    text = "⚠️ You have exceeded your monthly budget!"
                    visibility = TextView.VISIBLE
                }
                totalExpense >= 0.8 * totalBudget -> {
                    text = "⚠️ You have used over 80% of your budget."
                    visibility = TextView.VISIBLE
                }
                else -> visibility = TextView.GONE
            }
        }
    }

    private fun updatePieChart(pie: com.anychart.charts.Pie, totalIncome: Double, totalExpense: Double, balance: Double) {
        if (totalIncome == 0.0 && totalExpense == 0.0) {
            pie.title("No transactions recorded for this month.")
        } else {
            val data = mutableListOf<ValueDataEntry>().apply {
                add(ValueDataEntry("Income", totalIncome))
                add(ValueDataEntry("Expenses", totalExpense))
                if (balance != 0.0) add(ValueDataEntry("Balance", balance))
            }
            pie.data(data as List<DataEntry>)
            pie.title("Monthly Budget Overview")
        }
    }

    private fun filterCurrentMonth(transactions: List<Transaction>): List<Transaction> {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        val currentYear = calendar.get(Calendar.YEAR)
        val currentMonth = calendar.get(Calendar.MONTH)

        return transactions.filter {
            try {
                val date = sdf.parse(it.date)
                val transCal = Calendar.getInstance().apply { time = date!! }
                transCal.get(Calendar.YEAR) == currentYear && transCal.get(Calendar.MONTH) == currentMonth
            } catch (e: Exception) {
                Log.e("BudgetScreen", "Date parse error: ${it.date}", e)
                false
            }
        }
    }

    private fun setupNavigation() {
        findViewById<ImageView>(R.id.imageViewhome)?.setOnClickListener {
            startActivity(Intent(this, HomeScreen::class.java))
        }
        findViewById<ImageView>(R.id.imageViewprofile)?.setOnClickListener {
            startActivity(Intent(this, ProfileScreen::class.java))
        }
        findViewById<ImageView>(R.id.imageViewtransaction)?.setOnClickListener {
            startActivity(Intent(this, TransactionList::class.java))
        }
        findViewById<ImageView>(R.id.imageViewbudget)?.setOnClickListener {
            startActivity(Intent(this, BudgetScreen::class.java))
        }
    }
}