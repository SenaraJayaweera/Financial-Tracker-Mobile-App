package com.example.cashflow_app

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.AdapterView
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.cashflow_app.helpers.SharedPrefHelper
import java.io.File
import java.io.FileOutputStream

class ProfileScreen : AppCompatActivity() {

    lateinit var nameText: EditText
    lateinit var emailText: EditText
    lateinit var spinnerCurrency: Spinner

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile_screen)

        nameText = findViewById(R.id.editTextTextprofilename)
        emailText = findViewById(R.id.editTextTextEmailAddressprofileemail)
        spinnerCurrency = findViewById(R.id.spinnerCurrency)

        val sharedPref = getSharedPreferences("UserDetails", Context.MODE_PRIVATE)
        val name = sharedPref.getString("name", "N/A")
        val email = sharedPref.getString("email", "N/A")
        val savedCurrency = sharedPref.getString("currency", "USD")

        nameText.setText(name)
        emailText.setText(email)

        val currencyArray = resources.getStringArray(R.array.currency_options)
        val currencyPosition = currencyArray.indexOfFirst { it.contains(savedCurrency ?: "USD") }
        if (currencyPosition >= 0) {
            spinnerCurrency.setSelection(currencyPosition)
        }

        spinnerCurrency.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parentView: AdapterView<*>, view: android.view.View, position: Int, id: Long) {
                val selectedItem = parentView.getItemAtPosition(position) as String
                val currencyCode = when {
                    selectedItem.startsWith("Rs") -> "LKR"
                    selectedItem.startsWith("$") -> "USD"
                    selectedItem.startsWith("€") -> "EUR"
                    selectedItem.startsWith("£") -> "GBP"
                    selectedItem.startsWith("₹") -> "INR"
                    else -> "USD"
                }

                val editor = sharedPref.edit()
                editor.putString("currency", selectedItem)
                editor.apply()

                SharedPrefHelper.saveCurrencyType(this@ProfileScreen, currencyCode)
                sendBroadcast(Intent("CURRENCY_CHANGED"))
                Toast.makeText(this@ProfileScreen, "Currency changed to $currencyCode", Toast.LENGTH_SHORT).show()
            }

            override fun onNothingSelected(parentView: AdapterView<*>) {}
        }

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        findViewById<ImageView>(R.id.imageViewhome).setOnClickListener {
            startActivity(Intent(this, HomeScreen::class.java))
        }

        findViewById<ImageView>(R.id.imageViewprofile).setOnClickListener {
            startActivity(Intent(this, ProfileScreen::class.java))
        }

        findViewById<ImageView>(R.id.imageViewtransaction).setOnClickListener {
            startActivity(Intent(this, TransactionList::class.java))
        }

        findViewById<ImageView>(R.id.imageViewbudget).setOnClickListener {
            startActivity(Intent(this, BudgetScreen::class.java))
        }

        findViewById<Button>(R.id.btnExport).setOnClickListener { exportData() }
        findViewById<Button>(R.id.btnImport).setOnClickListener { importData() }

        val btnNavigate4: Button =findViewById(R.id.buttonsignout)
        btnNavigate4.setOnClickListener{
            AlertDialog.Builder(this)
                .setTitle("Confirm Sign Out")
                .setMessage("Do you really want to Sign Out?")
                .setPositiveButton("Yes"){
                        _,_ ->
                    Toast.makeText(this,"Sign Out Successfully",Toast.LENGTH_LONG).show()
                    val intent = Intent(this, AuthenticationScreen::class.java)
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("No",null)
                .show()
        }

        val btnNavigate5: Button =findViewById(R.id.buttondeleteacc)
        btnNavigate5.setOnClickListener{
            AlertDialog.Builder(this)
                .setTitle("Confirm Account Deletion")
                .setMessage("Do you really want to Delete Account?")
                .setPositiveButton("Yes"){
                        _,_ ->
                    Toast.makeText(this,"Delete Account Successfully",Toast.LENGTH_LONG).show()
                    val intent = Intent(this, AuthenticationScreen::class.java)
                    startActivity(intent)
                    finish()
                }
                .setNegativeButton("No",null)
                .show()
        }
    }

    private fun exportData() {
        try {
            val jsonData = SharedPrefHelper.exportDataToJson(this)
            val fileName = "cashflow_backup_${System.currentTimeMillis()}.json"

            openFileOutput(fileName, Context.MODE_PRIVATE).use {
                it.write(jsonData.toByteArray())
            }

            val downloadsDir = File(this.getExternalFilesDir(null), "Downloads")
            if (!downloadsDir.exists()) downloadsDir.mkdir()
            val file = File(downloadsDir, fileName)
            FileOutputStream(file).use {
                it.write(jsonData.toByteArray())
            }

            Toast.makeText(this, "Backup saved to $fileName", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Backup failed: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun importData() {
        val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        startActivityForResult(intent, IMPORT_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == IMPORT_REQUEST_CODE && resultCode == RESULT_OK) {
            data?.data?.let { uri ->
                try {
                    contentResolver.openInputStream(uri)?.use { inputStream ->
                        val json = inputStream.bufferedReader().use { it.readText() }
                        if (SharedPrefHelper.importDataFromJson(this, json)) {
                            Toast.makeText(this, "Data restored successfully", Toast.LENGTH_LONG).show()
                        } else {
                            Toast.makeText(this, "Invalid backup file", Toast.LENGTH_LONG).show()
                        }
                    }
                } catch (e: Exception) {
                    Toast.makeText(this, "Restore failed: ${e.message}", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    companion object {
        private const val IMPORT_REQUEST_CODE = 1001
    }
}
