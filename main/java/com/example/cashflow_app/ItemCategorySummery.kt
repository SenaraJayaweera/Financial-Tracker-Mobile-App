package com.example.cashflow_app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.cashflow_app.adapters.CategorySummaryAdapter
import com.example.cashflow_app.helpers.SharedPrefHelper
import com.example.cashflow_app.models.Transaction

class ItemCategorySummery : AppCompatActivity() {

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_category_summary)

        val recyclerView = findViewById<RecyclerView>(R.id.category_summary_recycler)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val transactions = SharedPrefHelper.getTransactions(this)
        val summaryList = getExpenseSummaryByCategory(transactions)
        recyclerView.adapter = CategorySummaryAdapter(summaryList)

        findViewById<ImageView>(R.id.imageViewback2).setOnClickListener {
            startActivity(Intent(this, HomeScreen::class.java))
        }
    }

    private fun getExpenseSummaryByCategory(transactions: List<Transaction>) =
        transactions.filter { it.type == "Expense" }
            .groupBy { it.category }
            .map { (category, items) ->
                com.example.cashflow_app.models.CategorySummary(category, items.sumOf { it.amount })
            }
}
