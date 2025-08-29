package com.example.cashflow_app

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.financial_tracker.models.Validations.LoginFormData
import com.example.financial_tracker.models.Validations.LoginFormValidationResult

class SignInScreen : AppCompatActivity() {

    lateinit var edtEmail: EditText
    lateinit var edtPassword: EditText
    private var count = 0;

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_in_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }



        val btnNavigate: Button = findViewById(R.id.buttonsigninsignup)
        btnNavigate.setOnClickListener {
            val intent = Intent(this, SignUpScreen::class.java)
            startActivity(intent)
        }
        edtEmail = findViewById(R.id.editTextTextEmailAddresssignin)
        edtPassword = findViewById(R.id.editTextTextPasswordsignin)
        val btnNavigate2: Button = findViewById(R.id.buttonsigninsignin)

        btnNavigate2.setOnClickListener {
            submit(it)
        }

    }
    fun displayAleart(title: String, message: String) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK") { dialog, which ->
            Toast.makeText(this, "All details match the expected criteria", Toast.LENGTH_LONG)
                .show()

            val intent = Intent(this, HomeScreen::class.java)
            startActivity(intent)
            finish() // Close SignInScreen
        }

        val dialog = builder.create()
        dialog.show()
    }

    fun submit(view: View) {
        count = 0
        val myForm = LoginFormData(
            edtEmail.text.toString(),
            edtPassword.text.toString()
        )

        val emailValidation = myForm.validateEmail()
        val passwordValidation = myForm.validatePassword()

        when (emailValidation) {
            is LoginFormValidationResult.Valid -> {
                count++
            }

            is LoginFormValidationResult.Invalid -> {
                edtEmail.error = emailValidation.errorMessage
            }

            is LoginFormValidationResult.Empty -> {
                edtEmail.error = emailValidation.errorMessage
            }
        }

        when (passwordValidation) {
            is LoginFormValidationResult.Valid -> {
                count++
            }

            is LoginFormValidationResult.Invalid -> {
                edtPassword.error = passwordValidation.errorMessage
            }

            is LoginFormValidationResult.Empty -> {
                edtPassword.error = passwordValidation.errorMessage
            }
        }

        if (count == 2) {
            displayAleart("Success", "You have successfully Logined")

        }


    }
    }