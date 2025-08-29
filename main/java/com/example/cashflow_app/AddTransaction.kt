package com.example.cashflow_app

import android.app.DatePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.example.cashflow_app.helpers.SharedPrefHelper
import com.example.cashflow_app.models.Transaction
import java.text.SimpleDateFormat
import java.util.*

class AddTransaction : AppCompatActivity() {

    private lateinit var titleInput: EditText
    private lateinit var amountInput: EditText
    private lateinit var dateDisplay: TextView
    private lateinit var incomeRadio: RadioButton
    private lateinit var expenseRadio: RadioButton
    private lateinit var categorySpinner: Spinner
    private lateinit var saveButton: Button

    private var selectedDate: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_transaction)

        // Link your UI
        titleInput = findViewById(R.id.editTextTexttitle)
        amountInput = findViewById(R.id.amount_input)
        dateDisplay = findViewById(R.id.date_display)
        incomeRadio = findViewById(R.id.income_radio)
        expenseRadio = findViewById(R.id.expense_radio)
        categorySpinner = findViewById(R.id.category_spinner)
        saveButton = findViewById(R.id.save_transaction_button)

        setCurrentDate()

        // Click date to select
        dateDisplay.setOnClickListener { showDatePicker() }

        // Save button logic
        saveButton.setOnClickListener { saveTransaction() }

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

    private fun setCurrentDate() {
        val calendar = Calendar.getInstance()
        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        selectedDate = formatter.format(calendar.time)
        dateDisplay.text = selectedDate
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

    private fun saveTransaction() {
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

        val transaction = Transaction(
            id = System.currentTimeMillis(),
            title = title,
            amount = amount,
            date = selectedDate,
            type = type,           // Just "Income" or "Expense"
            category = category    // Just the category like "Food", "Rent"
        )


        // Fetch current transactions
        val currentTransactions = SharedPrefHelper.getTransactions(this).toMutableList()
        currentTransactions.add(transaction)

        // Save the new transactions and update the budget
        SharedPrefHelper.saveTransactions(this, currentTransactions)

        // Update the budget
        //SharedPrefHelper.updateBudget(this, currentTransactions)


        // Show updated budget
        val updatedBudget = SharedPrefHelper.calculateCurrentBudget(this)
        Toast.makeText(
            this,
            "Transaction saved! Updated Budget: Rs. $updatedBudget",
            Toast.LENGTH_SHORT
        ).show()

        // Navigate back to your transaction list or another screen
        val intent = Intent(this, TransactionList::class.java)
        startActivity(intent)
        finish()
    }

}
