package com.example.cashflow_app

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.cashflow_app.helpers.SharedPrefHelper
import com.example.cashflow_app.models.Transaction
import java.text.SimpleDateFormat
import java.util.*

class EditTransaction : AppCompatActivity() {

    private lateinit var titleInput: EditText
    private lateinit var amountInput: EditText
    private lateinit var dateDisplay: TextView
    private lateinit var incomeRadio: RadioButton
    private lateinit var expenseRadio: RadioButton
    private lateinit var categorySpinner: Spinner
    private lateinit var updateButton: Button

    private var selectedDate: String = ""
    private var transactionId: Long = -1L

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_transaction)

        titleInput = findViewById(R.id.editTextTexttitle)
        amountInput = findViewById(R.id.amount_input)
        dateDisplay = findViewById(R.id.date_display)
        incomeRadio = findViewById(R.id.income_radio)
        expenseRadio = findViewById(R.id.expense_radio)
        categorySpinner = findViewById(R.id.category_spinner)
        updateButton = findViewById(R.id.update_transaction_button)

        transactionId = intent.getLongExtra("transaction_id", -1L)

        loadTransaction()

        dateDisplay.setOnClickListener { showDatePicker() }

        updateButton.setOnClickListener {
            updateTransaction()
        }

        findViewById<ImageView>(R.id.imageViewhome).setOnClickListener {
            startActivity(Intent(this, HomeScreen::class.java))
        }

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
    }

    private fun loadTransaction() {
        val transactions = SharedPrefHelper.getTransactions(this)
        val transaction = transactions.find { it.id == transactionId }

        if (transaction != null) {
            titleInput.setText(transaction.title)
            amountInput.setText(transaction.amount.toString())
            selectedDate = transaction.date
            dateDisplay.text = selectedDate

            if (transaction.type == "Income") {
                incomeRadio.isChecked = true
            } else {
                expenseRadio.isChecked = true
            }

            val adapter = categorySpinner.adapter
            for (i in 0 until adapter.count) {
                if (adapter.getItem(i).toString() == transaction.category) {
                    categorySpinner.setSelection(i)
                    break
                }
            }
        } else {
            Toast.makeText(this, "Transaction not found", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                val selectedCalendar = Calendar.getInstance()
                selectedCalendar.set(year, month, dayOfMonth)
                val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                selectedDate = formatter.format(selectedCalendar.time)
                dateDisplay.text = selectedDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun updateTransaction() {
        val title = titleInput.text.toString().trim()
        val amountText = amountInput.text.toString().trim()
        val category = categorySpinner.selectedItem?.toString()?.trim() ?: ""

        if (title.isEmpty() || amountText.isEmpty() || category.isEmpty()) {
            Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val amount = amountText.toDoubleOrNull() ?: 0.0
        if (amount <= 0) {
            Toast.makeText(this, "Please enter a valid amount", Toast.LENGTH_SHORT).show()
            return
        }

        val type = if (incomeRadio.isChecked) "Income" else "Expense"

        val updatedTransaction = Transaction(
            id = transactionId,
            title = title,
            amount = amount,
            date = selectedDate,
            type = type,
            category = category
        )

        val transactions = SharedPrefHelper.getTransactions(this).toMutableList()
        val index = transactions.indexOfFirst { it.id == transactionId }

        if (index != -1) {
            transactions[index] = updatedTransaction
            SharedPrefHelper.saveTransactions(this, transactions)

            val updatedBudget = SharedPrefHelper.calculateCurrentBudget(this)
            Toast.makeText(this, "Transaction updated. New Budget: Rs. $updatedBudget", Toast.LENGTH_SHORT).show()

            startActivity(Intent(this, TransactionList::class.java))
            finish()
        } else {
            Toast.makeText(this, "Transaction not found", Toast.LENGTH_SHORT).show()
        }
    }
}
