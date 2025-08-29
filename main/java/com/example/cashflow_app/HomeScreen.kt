package com.example.cashflow_app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cashflow_app.helpers.SharedPrefHelper
import com.example.cashflow_app.models.CategorySummary
import com.example.cashflow_app.models.Transaction

class HomeScreen : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter
    private lateinit var tvBudget: TextView
    private var transactions: MutableList<Transaction> = mutableListOf()

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home_screen)

        // Initialize views
        tvBudget = findViewById(R.id.textViewbudget)
        recyclerView = findViewById(R.id.recyclerTransactions)

        // Set RecyclerView LayoutManager
        recyclerView.layoutManager = LinearLayoutManager(this)

        // Initialize the transactionAdapter before setting it to RecyclerView
        transactionAdapter = TransactionAdapter(transactions) {
            loadTransactions()
        }

        // Set the adapter to RecyclerView
        recyclerView.adapter = transactionAdapter

        // Set click listener for the profile image
        findViewById<ImageView>(R.id.imageViewprofile).setOnClickListener {
            startActivity(Intent(this, ProfileScreen::class.java))
        }

        // Set click listener for the transaction image
        findViewById<ImageView>(R.id.imageViewtransaction).setOnClickListener {
            startActivity(Intent(this, TransactionList::class.java))
        }

        findViewById<ImageView>(R.id.imageViewbudget).setOnClickListener {
            startActivity(Intent(this, BudgetScreen::class.java))
        }

        findViewById<Button>(R.id.btnViewSummary).setOnClickListener {
            startActivity(Intent(this, ItemCategorySummery::class.java))
        }

        // Load transactions when the activity is created
        loadTransactions()
    }

    override fun onResume() {
        super.onResume()
        // Refresh the transactions when the activity resumes
        loadTransactions()
    }

    private fun loadTransactions() {
        // Clear the existing transaction list and load the updated transactions from SharedPreferences
        transactions.clear()
        transactions.addAll(SharedPrefHelper.getTransactions(this))

        // Update the displayed budget and check for exceeding budget
        updateBudgetDisplay()

        // Notify the adapter that the data has changed
        transactionAdapter.notifyDataSetChanged()
    }

    private fun updateBudgetDisplay() {
        // Calculate the current budget using SharedPreferences helper
        val budget = SharedPrefHelper.calculateCurrentBudget(this)
        val initialBudget = SharedPrefHelper.getInitialBudget(this)

        // Set the budget text to the TextView
        tvBudget.text = "${SharedPrefHelper.getCurrencyType(this)} %.2f".format(budget)

        // Check if the budget exceeds the initial budget
        if (budget < initialBudget) {
            showBudgetExceededWarning()
        } else if (budget < initialBudget * 0.8) {
            showWarningAboutBudget()
        }
    }

    private fun showBudgetExceededWarning() {
        Toast.makeText(this, "You have exceeded your budget!", Toast.LENGTH_LONG).show()
    }

    private fun showWarningAboutBudget() {
        Toast.makeText(this, "You are approaching your budget limit!", Toast.LENGTH_SHORT).show()
    }
}

