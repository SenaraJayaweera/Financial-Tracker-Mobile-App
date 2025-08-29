package com.example.cashflow_app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cashflow_app.helpers.SharedPrefHelper
import com.example.cashflow_app.models.CategorySummary
import com.example.cashflow_app.models.Transaction
import kotlin.collections.filter

class TransactionList : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var transactionAdapter: TransactionAdapter

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_transaction_list)

        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)

        setupList()

        findViewById<Button>(R.id.addTransactionButton).setOnClickListener {
            startActivity(Intent(this, AddTransaction::class.java))
        }

        findViewById<ImageView>(R.id.imageViewback).setOnClickListener {
            startActivity(Intent(this, HomeScreen::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        setupList() // reload data when returning to this screen
    }

    private fun setupList() {
        val transactions = SharedPrefHelper.getTransactions(this).toMutableList()
        transactionAdapter = TransactionAdapter(transactions) {
            setupList() // refresh on change
        }
        recyclerView.adapter = transactionAdapter
    }

    fun getExpenseSummaryByCategory(transactions: List<Transaction>): List<CategorySummary> {
        return transactions
            .filter { it.type == "Expense" }
            .groupBy { it.category }
            .map { (category, items) ->
                CategorySummary(category, items.sumOf { it.amount })
            }
    }
}
