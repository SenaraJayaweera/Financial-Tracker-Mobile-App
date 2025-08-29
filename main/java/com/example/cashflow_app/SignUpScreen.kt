package com.example.cashflow_app

import android.annotation.SuppressLint
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.financial_tracker.models.Validations.SignUpFormData
import com.example.financial_tracker.models.Validations.SignUpFormValidationResult

class SignUpScreen : AppCompatActivity() {

    lateinit var edtName: EditText
    lateinit var edtEmail: EditText
    lateinit var password: EditText
    lateinit var reenterPassword: EditText
    lateinit var cbAgree: CheckBox
    private var count = 0;

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_sign_up_screen)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnNavigate: Button =findViewById(R.id.buttonsignupsignin)
        btnNavigate.setOnClickListener{
            val intent= Intent(this,SignInScreen::class.java)
            startActivity(intent)
        }

        edtName = findViewById(R.id.editTextTextsignupname)
        edtEmail = findViewById(R.id.editTextTextEmailAddresssignup)
        password = findViewById(R.id.editTextTextPasswordsignup)
        reenterPassword = findViewById(R.id.editTextTextPasswordsignupconfirm)
        cbAgree = findViewById(R.id.checkBoxsignup)

        val btnNavigate1: Button = findViewById(R.id.buttonsignuppagesignup)
        btnNavigate1.setOnClickListener {
            submit(it)
        }
    }
    fun displayAlert(title:String,message:String, shouldNavigate:Boolean){
        val builder = AlertDialog.Builder(this)
        builder.setTitle(title)
        builder.setMessage(message)
        builder.setPositiveButton("OK"){
                dialog, which->
            if(shouldNavigate) {
                Toast.makeText(
                    this, "All details match the expected criteria",
                    Toast.LENGTH_LONG
                ).show()

                val intent = Intent(this, SignInScreen::class.java)
                startActivity(intent)
                finish()

            }
        }

        val dialog=builder.create()
        dialog.show()
    }

    fun submit(v: View){
        count = 0

        val myForm = SignUpFormData(
            edtName.text.toString(),
            edtEmail.text.toString(),
            password.text.toString(),
            reenterPassword.text.toString(),
            cbAgree.isChecked
        )

        val nameValidation = myForm.validateName()
        val emailValidation = myForm.validateEmail()
        val passwordValidation=myForm.validatePassword()
        val reenterPasswordValidation=myForm.validReEnterPassword()
        val cbAgreeValidation=myForm.validateAgreement()

        when(nameValidation){
            is SignUpFormValidationResult.Valid ->{
                count ++
            }
            is SignUpFormValidationResult.Invalid->{
                edtName.error = nameValidation.errorMessage
            }
            is SignUpFormValidationResult.Empty ->{
                edtName.error = nameValidation.errorMessage
            }
        }

        when(emailValidation){
            is SignUpFormValidationResult.Valid ->{
                count ++
            }
            is SignUpFormValidationResult.Invalid->{
                edtEmail.error = emailValidation.errorMessage
            }
            is SignUpFormValidationResult.Empty ->{
                edtEmail.error = emailValidation.errorMessage
            }
        }

        when(passwordValidation){
            is SignUpFormValidationResult.Valid ->{
                count ++
            }
            is SignUpFormValidationResult.Invalid->{
                password.error = passwordValidation.errorMessage
            }
            is SignUpFormValidationResult.Empty ->{
                password.error = passwordValidation.errorMessage
            }
        }

        when(reenterPasswordValidation){
            is SignUpFormValidationResult.Valid ->{
                count ++
            }
            is SignUpFormValidationResult.Invalid->{
                reenterPassword.error = reenterPasswordValidation.errorMessage
            }
            is SignUpFormValidationResult.Empty ->{
                reenterPassword.error = reenterPasswordValidation.errorMessage
            }
        }

        when(cbAgreeValidation){
            is SignUpFormValidationResult.Valid ->{
                count ++
            }
            is SignUpFormValidationResult.Invalid->{
                displayAlert("Error",cbAgreeValidation.errorMessage,false)

            }
            is SignUpFormValidationResult.Empty ->{

            }
        }
        if(count==5){
            val sharedPref = getSharedPreferences("UserDetails",MODE_PRIVATE)
            val editor = sharedPref.edit()
            editor.putString("name",myForm.name)
            editor.putString("email", myForm.email)
            editor.putString("password", myForm.password)
            editor.apply()

            displayAlert("Success","You have successfully registerd",true)
        }
    }

}
