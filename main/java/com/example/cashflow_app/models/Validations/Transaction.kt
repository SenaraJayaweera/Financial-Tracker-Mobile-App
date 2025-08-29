package com.example.cashflow_app.models

data class Transaction (
    val id: Long,
    val title: String,
    val amount: Double,
    val date: String,
    val type: String, // "Income" or "Expense"
    val category: String
)
